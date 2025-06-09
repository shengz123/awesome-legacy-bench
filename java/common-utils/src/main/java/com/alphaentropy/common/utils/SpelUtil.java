package com.alphaentropy.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class SpelUtil {

    public static Boolean valueOfBooleanExpr(final Map data, final String expr) {
        try {
            return (Boolean) valueOf(data, expr);
        } catch (Exception e) {
            log.error("Failed to get value of {} with {}", expr, data);
        }
        return false;
    }

    private static String encodeVar(String var) {
        return var.replace(".", "_");
    }

    public static Object valueOf(final Map data, final String expr) {
        String newExpr = expr;
        Map newCtx = new HashMap();
        for (Object var : data.keySet()) {
            String newVar = encodeVar(var.toString());
            newExpr = newExpr.replace(var.toString(), newVar);
            newCtx.put(newVar, data.get(var));
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(newCtx);
        SpelExpression exp = (SpelExpression) new SpelExpressionParser().parseExpression(newExpr);
        return exp.getValue(context);
    }

    public static Set<String> parseVariables(final String expr) {
        Set<String> ret = new HashSet<>();
        if (expr != null && !expr.isEmpty()) {
            SpelExpression exp = (SpelExpression) new SpelExpressionParser().parseExpression(expr);
            traverseGatherVariables(ret, exp.getAST());
        }
        return ret;
    }

    private static void traverseGatherVariables(Set<String> variables, SpelNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            SpelNode child = node.getChild(i);
            if (child instanceof VariableReference) {
                VariableReference variable = (VariableReference) child;
                try {
                    Field field = variable.getClass().getDeclaredField("name");
                    field.setAccessible(true);
                    String variableName = field.get(variable).toString();
                    variables.add(variableName);
                } catch (Exception e) {
                    log.error("Failed to find name of expression variable: {}", variable.toString(), e);
                }
            } else if (child instanceof CompoundExpression) {
                int cnt = child.getChildCount();
                if (cnt == 2) {
                    try {
                        Field fld1 = child.getChild(0).getClass().getDeclaredField("name");
                        Field fld2 = child.getChild(1).getClass().getDeclaredField("name");
                        fld1.setAccessible(true);
                        fld2.setAccessible(true);
                        String var = fld1.get(child.getChild(0)).toString() + "." + fld2.get(child.getChild(1)).toString();
                        variables.add(var);
                    } catch (Exception e) {
                        log.error("Failed to find name of expression variable: {}", child.toString(), e);
                    }
                }
            } else {
                traverseGatherVariables(variables, child);
            }
        }
    }
}
