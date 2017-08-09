package com.github.ilms49898723.minttranslator.ucf;

import com.github.ilms49898723.minttranslator.symbols.Operator;
import com.github.ilms49898723.minttranslator.translator.MINTConfiguration;
import com.github.ilms49898723.minttranslator.translator.StatusCode;
import com.github.ilms49898723.minttranslator.translator.SymbolTable;

import javax.json.*;
import javax.json.stream.JsonParsingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * UCF Processor.
 */
public class UCFProcessor {
    private String mFilename;
    private SymbolTable mSymbolTable;
    private MINTConfiguration mConfiguration;

    public UCFProcessor(String ucfFilename, SymbolTable symbolTable, MINTConfiguration configuration) {
        mFilename = ucfFilename;
        mSymbolTable = symbolTable;
        mConfiguration = configuration;
        File ucfFile = new File(ucfFilename);
        if (!ucfFile.exists() || !ucfFile.isFile()) {
            System.err.println(ucfFile + ": invalid UCF file.");
            System.exit(1);
        }
    }

    public StatusCode setDefaults() {
        Operator valveOperator = new Operator("/");
        valveOperator.setName("Valve");
        valveOperator.setLayer("control");
        valveOperator.setMINT("VALVE #NAME ON #TARGET");
        valveOperator.setInputs(1);
        valveOperator.setOutputs(0);
        valveOperator.setInputTerms(Collections.singletonList(1));
        valveOperator.setOutputTerms(new ArrayList<>());
        mSymbolTable.put(valveOperator);
        return StatusCode.SUCCESS;
    }

    public StatusCode parse() {
        StatusCode statusCode = StatusCode.SUCCESS;
        try (JsonReader jsonReader = Json.createReader(new FileInputStream(mFilename))) {
            JsonArray ucfArray = jsonReader.readArray();
            for (int i = 0; i < ucfArray.size(); ++i) {
                JsonObject ucfObject = ucfArray.getJsonObject(i);
                if (!ucfObject.containsKey("operator")) {
                    System.err.println("In UCF file:");
                    System.err.println("Invalid: A Json object without operator key.");
                    System.err.println(ucfObject);
                    statusCode = StatusCode.INVALID_UCF;
                    continue;
                }
                String operator = ucfObject.getString("operator");
                StatusCode code;
                if (operator.equals("general information")) {
                    code = parseGeneralInformation(ucfObject);
                } else {
                    code = parseOperator(ucfObject);
                }
                if (code != StatusCode.SUCCESS) {
                    statusCode = code;
                }
            }
        } catch (JsonException e) {
            System.err.println("UCF json file parsing error.");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return statusCode;
    }

    private StatusCode parseGeneralInformation(JsonObject ucfObject) {
        for (String key : ucfObject.keySet()) {
            if (key.equals("operator")) {
                continue;
            }
            int value = ucfObject.getInt(key);
            StatusCode statusCode = mConfiguration.put(key, String.valueOf(value));
            if (statusCode != StatusCode.SUCCESS) {
                System.err.println("In UCF file:");
                System.err.println("At General Information key: " + key);
                System.err.println("Error occurred when recording configurations.");
                return StatusCode.INVALID_UCF;
            }
        }
        return StatusCode.SUCCESS;
    }

    private StatusCode parseOperator(JsonObject ucfObject) {
        String[] keysNeeded = {
                "operator",
                "name",
                "inputs",
                "outputs",
                "inputTerms",
                "outputTerms",
                "mint",
                "layer"
        };
        for (String key : keysNeeded) {
            if (!ucfObject.containsKey(key)) {
                System.err.println("In UCF file:");
                System.err.println("At operator: " + ucfObject.getString("operator"));
                System.err.println("Key " + key + " is necessary in operator Json Object.");
                return StatusCode.INVALID_UCF;
            }
        }
        Operator operator = new Operator(ucfObject.getString("operator", ""));
        operator.setName(ucfObject.getString("name", ""));
        operator.setInputs(ucfObject.getInt("inputs", 0));
        operator.setOutputs(ucfObject.getInt("outputs", 0));
        operator.setControlInputTerm(ucfObject.getInt("controlTerm", -1));
        operator.setMINT(ucfObject.getString("mint", ""));
        operator.setLayer(ucfObject.getString("layer", ""));
        if (!operator.getLayer().equals("flow") && !operator.getLayer().equals("control")) {
            System.err.println("In UCF file:");
            System.err.println("At operator " + operator.getName());
            System.err.println("Invalid layer " + operator.getLayer());
            System.err.println("Should be either flow or control.");
            return StatusCode.INVALID_UCF;
        }
        List<Integer> inputTerms = new ArrayList<>();
        String[] inputTermTokens = ucfObject.getString("inputTerms").split("[ ,]");
        for (String term : inputTermTokens) {
            if (term.isEmpty()) {
                continue;
            }
            inputTerms.add(Integer.parseInt(term));
        }
        operator.setInputTerms(inputTerms);
        List<Integer> outputTerms = new ArrayList<>();
        String[] outputTermTokens = ucfObject.getString("outputTerms").split("[ ,]");
        for (String term : outputTermTokens) {
            if (term.isEmpty()) {
                continue;
            }
            outputTerms.add(Integer.parseInt(term));
        }
        operator.setOutputTerms(outputTerms);
        mSymbolTable.put(operator);
        return StatusCode.SUCCESS;
    }
}
