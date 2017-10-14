package com.github.ilms49898723.minttranslator.ucf;

import com.github.ilms49898723.minttranslator.errorhandling.ErrorCode;
import com.github.ilms49898723.minttranslator.errorhandling.ErrorHandler;
import com.github.ilms49898723.minttranslator.symbols.Operator;
import com.github.ilms49898723.minttranslator.translator.MINTConfiguration;
import com.github.ilms49898723.minttranslator.translator.StatusCode;
import com.github.ilms49898723.minttranslator.translator.SymbolTable;

import javax.json.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
            ErrorHandler.printErrorMessageAndExit(mFilename, null, ErrorCode.INVALID_UCF_FILE);
        }
    }

    public StatusCode parse() {
        StatusCode statusCode = StatusCode.SUCCESS;
        try (JsonReader jsonReader = Json.createReader(new FileInputStream(mFilename))) {
            JsonArray ucfArray = jsonReader.readArray();
            for (int i = 0; i < ucfArray.size(); ++i) {
                JsonObject ucfObject = ucfArray.getJsonObject(i);
                if (!ucfObject.containsKey("module")) {
                    System.err.println("In file " + mFilename);
                    System.err.println("Invalid module: a Json object without module key.");
                    System.err.println(ucfObject);
                    statusCode = StatusCode.FAIL;
                    continue;
                }
                String operator = ucfObject.getString("module");
                StatusCode code;
                if (operator.equals("general information")) {
                    code = parseGeneralInformation(ucfObject);
                } else {
                    code = parseModule(ucfObject);
                }
                if (code != StatusCode.SUCCESS) {
                    statusCode = code;
                }
            }
        } catch (JsonException e) {
            System.err.println(mFilename + ": json file parsing error.");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Operator node = new Operator("node");
        mSymbolTable.put(node);
        return statusCode;
    }

    private StatusCode parseGeneralInformation(JsonObject ucfObject) {
        for (String key : ucfObject.keySet()) {
            if (key.equals("module")) {
                continue;
            }
            int value = ucfObject.getInt(key);
            StatusCode statusCode = mConfiguration.put(key, value);
            if (statusCode != StatusCode.SUCCESS) {
                System.err.println("In file " + mFilename);
                System.err.println("At General Information key: " + key);
                System.err.println("Invalid key or values.");
                return StatusCode.FAIL;
            }
        }
        return StatusCode.SUCCESS;
    }

    private StatusCode parseModule(JsonObject ucfObject) {
        String[] keysNeeded = {
                "module",
                "inputTerms",
                "outputTerms",
                "mint",
                "layer"
        };
        for (String key : keysNeeded) {
            if (!ucfObject.containsKey(key)) {
                System.err.println("In file " + mFilename);
                System.err.println("At operator: " + ucfObject.getString("module"));
                System.err.println("Key " + key + " is necessary in a module json Object.");
                return StatusCode.FAIL;
            }
        }
        if (!ucfObject.getString("layer").equals("flow")
                && !ucfObject.getString("layer").equals("control")) {
            System.err.println("In file " + mFilename);
            System.err.println("At operator " + ucfObject.getString("name"));
            System.err.println("Invalid layer " + ucfObject.getString("layer"));
            System.err.println("Should be either \'flow\' or \'control\'.");
            return StatusCode.FAIL;
        }
        Operator operator = new Operator(ucfObject.getString("module"));
        operator.setMINT(ucfObject.getString("mint"));
        operator.setLayer(ucfObject.getString("layer"));
        List<Integer> inputTerms = new ArrayList<>();
        String[] inputTermTokens = ucfObject.getString("inputTerms").split("[ ,]");
        for (String term : inputTermTokens) {
            if (term.isEmpty()) {
                continue;
            }
            inputTerms.add(Integer.parseInt(term));
        }
        operator.setInputTerms(inputTerms);
        operator.setInputs(inputTerms.size());
        List<Integer> outputTerms = new ArrayList<>();
        String[] outputTermTokens = ucfObject.getString("outputTerms").split("[ ,]");
        for (String term : outputTermTokens) {
            if (term.isEmpty()) {
                continue;
            }
            outputTerms.add(Integer.parseInt(term));
        }
        operator.setOutputTerms(outputTerms);
        operator.setOutputs(outputTerms.size());
        StatusCode code = mSymbolTable.put(operator);
        if (code != StatusCode.SUCCESS) {
            System.err.println("In file " + mFilename);
            System.err.println("Module " + operator.getIdentifier() + " is already defined.");
            return StatusCode.FAIL;
        }
        return StatusCode.SUCCESS;
    }
}
