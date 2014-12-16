package com.github.hiteshsondhi88.libffmpeg;

import java.io.IOException;
import java.util.ArrayList;

class ShellCommand {

    Process run(String commandString) {
        Process process = null;

        boolean foundDoubleQuotes = false;
        String cmdPart = "";
        ArrayList<String> tempList = new ArrayList<String>();
        for (int i=0;i<commandString.length();i++){
            if (commandString.charAt(i)==' ' && !foundDoubleQuotes) {
                tempList.add(cmdPart);
                cmdPart = "";
            } else if (commandString.charAt(i)=='"') {
                foundDoubleQuotes = !foundDoubleQuotes;
            } else {
                cmdPart+=commandString.charAt(i);
            }
        }
        if (cmdPart.length()>0) {
            tempList.add(cmdPart);
        }
        String[] command = tempList.toArray(new String[tempList.size()]);

        for (String c : command){
            Log.d("commandPart: "+c);
        }

        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            Log.e("Exception while trying to run: " + commandString, e);
        }
        return process;
    }

    CommandResult runWaitFor(String s) {
        Process process = run(s);

        Integer exitValue = null;
        String output = null;
        try {
            if (process != null) {
                exitValue = process.waitFor();

                if (CommandResult.success(exitValue)) {
                    output = Util.convertInputStreamToString(process.getInputStream());
                } else {
                    output = Util.convertInputStreamToString(process.getErrorStream());
                }
            }
        } catch (InterruptedException e) {
            Log.e("Interrupt exception", e);
        } finally {
            Util.destroyProcess(process);
        }

        return new CommandResult(CommandResult.success(exitValue), output);
    }

}