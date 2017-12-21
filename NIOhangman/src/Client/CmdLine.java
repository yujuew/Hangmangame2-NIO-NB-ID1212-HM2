package Client;

public class CmdLine {
    String MSG_DELIMITER = " ";
    private Command cmd;
    private String[] args;

    CmdLine(String inputString){
        retrieveCmd(inputString);
        retrieveParameters(inputString);
    }

    private void retrieveCmd(String input){
        try{
            String[] arguments = input.split(MSG_DELIMITER);
            cmd = Command.valueOf(arguments[0].toUpperCase());
        }
        catch(Exception e){
            cmd = Command.GUESS;
        }
    }

    private void retrieveParameters(String input){
        args = removeCommand(input).split(MSG_DELIMITER);
    }

    public Command getCmd() {
        return cmd;
    }

    public String[] getArgs() {
        return args;
    }

    private String removeCommand(String input){
        if(cmd != Command.CONNECT){
            return input;
        }
        int len = cmd.toString().length() + 1; // +1 to include the space between each letter.
        String withoutCMD = input.substring(len);
        return withoutCMD;
    }
}
