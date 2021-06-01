import edu.ucr.cs.riple.diagnose.metadata.MethodInheritanceTree;
import edu.ucr.cs.riple.injector.Injector;
import edu.ucr.cs.riple.injector.WorkListBuilder;
import edu.ucr.cs.riple.diagnose.DiagnoseJar;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
//    if(args.length == 0){
//      throw new RuntimeException("command not specified");
//    }
//    String command = args[0];
//    switch (command){
//      case "apply":
//        apply(args);
//        break;
//      case "diagnose":
//        diagnose(args);
//        break;
//      default:
//        throw new RuntimeException("Unknown command: " + command);
//    }
    MethodInheritanceTree m = new MethodInheritanceTree("/tmp/NullAwayFix/method_info.json");
    String method = "adapt(retrofit2.Call<R>)";
    String clazz = "retrofit2.CallAdapter";
    System.out.println(m.getSubMethods(method, clazz));
  }

  private static void diagnose(String[] args) {
    DiagnoseJar diagnose = new DiagnoseJar();
    System.out.println("Number of received arguments: " + args.length);
    System.out.println("Actual Arguments: " + Arrays.toString(args));
    if (!(args.length == 3 || args.length == 4)) {
      throw new RuntimeException(
              "Diagnose needs two/three arguments: 1. command to execute NullAway, " +
                      "2. output directory, 3. optimized [optional]");
    }
    boolean optimized = args.length == 4 && Boolean.getBoolean(args[3]);
    String dir = args[1];
    String runCommand = args[2];
    diagnose.start(runCommand, dir, optimized);
  }

  private static void apply(String[] args) {
    System.out.println("Number of received arguments: " + args.length);
    System.out.println("Actual Arguments: " + Arrays.toString(args));
    if (args.length != 2) {
      throw new RuntimeException(
              "Diagnose needs exactly one arguments: 1. path to the suggested fix file");
    }
    System.out.println("Building Injector...");
    Injector injector = Injector.builder().setMode(Injector.MODE.BATCH).build();
    System.out.println("built.");
    System.out.println("Injecting...");
    injector.start(new WorkListBuilder(args[1]).getWorkLists());
    System.out.println("Finished");
  }
}
