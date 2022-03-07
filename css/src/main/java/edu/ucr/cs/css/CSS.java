package edu.ucr.cs.css;

import static com.google.errorprone.BugPattern.SeverityLevel.SUGGESTION;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.ErrorProneFlags;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.code.Symbol;
import edu.ucr.cs.css.out.MethodInfo;
import edu.ucr.cs.css.out.TrackerNode;

import javax.lang.model.element.ElementKind;
import java.util.ArrayList;
import java.util.List;

@AutoService(BugChecker.class)
@BugPattern(
    name = "CSS",
    altNames = {"TypeBasedStructureSerializer"},
    summary = "Serialized type based call/field graph.",
    tags = BugPattern.StandardTags.STYLE,
    severity = SUGGESTION)
public class CSS extends BugChecker
    implements BugChecker.MethodInvocationTreeMatcher,
        BugChecker.AssignmentTreeMatcher,
        BugChecker.MemberSelectTreeMatcher,
        BugChecker.MethodTreeMatcher{

  private final Config config;

  public CSS() {
    this.config = new Config();
  }

  public CSS(ErrorProneFlags flags) {
    this.config = new Config(flags);
  }

  @Override
  public Description matchAssignment(AssignmentTree tree, VisitorState state) {
    if (!config.fieldTrackerIsActive) {
      return Description.NO_MATCH;
    }
    if (tree == null) {
      return Description.NO_MATCH;
    }
    Symbol expressionSym = ASTHelpers.getSymbol(tree.getExpression());
    if (expressionSym != null && expressionSym.getKind() == ElementKind.FIELD) {
      config.serializer.serializeFieldGraphNode(new TrackerNode(ASTHelpers.getSymbol(tree), state.getPath()));
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchMemberSelect(MemberSelectTree tree, VisitorState state) {
    if(!config.fieldTrackerIsActive){
      return Description.NO_MATCH;
    }
    Symbol symbol = ASTHelpers.getSymbol(tree);
    if (symbol != null && symbol.getKind().equals(ElementKind.FIELD)) {
      config.serializer.serializeFieldGraphNode(new TrackerNode(symbol, state.getPath()));
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (config.methodTrackerIsActive) {
      config.serializer.serializeCallGraphNode(new TrackerNode(ASTHelpers.getSymbol(tree), state.getPath()));
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchMethod(MethodTree tree, VisitorState state) {
    Symbol.MethodSymbol methodSymbol = ASTHelpers.getSymbol(tree);
    String method = methodSymbol.toString();
    String clazz = ASTHelpers.enclosingClass(methodSymbol).toString();
    MethodInfo methodInfo = MethodInfo.findOrCreate(method, clazz);
//    methodInfo.setUri(c);
    methodInfo.setParent(methodSymbol, state);
    methodInfo.setParamNumber(methodSymbol.getParameters().size());
    List<Boolean> paramAnnotations = new ArrayList<>();
    for (int i = 0; i < methodSymbol.getParameters().size(); i++) {
      paramAnnotations.add(SymbolUtil.paramHasNullableAnnotation(methodSymbol, i, config));
    }
    methodInfo.setParamAnnotations(paramAnnotations);
    config.serializer.serializeMethodInfo(methodInfo);
    return null;
  }
}
