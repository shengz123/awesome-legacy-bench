package com.alphaentropy.factor.core.application.dependency;

import com.alphaentropy.factor.core.domain.CompiledFormula;
import com.alphaentropy.factor.core.domain.FormulaCall;
import com.alphaentropy.factor.core.domain.FormulaCallGroup;
import com.alphaentropy.factor.core.domain.FormulaOutput;
import com.alphaentropy.factor.core.infrastructure.dao.FormulaCallGroupDAO;
import com.alphaentropy.factor.core.infrastructure.dao.FormulaDAO;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Lazy
@Component
@Slf4j
public class FormulaCallResolver {
    @Autowired
    private FormulaDAO formulaDAO;

    @Autowired
    private FormulaCallGroupDAO callGroupDAO;

    public ResolveResult resolveDependencies(String callGroupName) {
        FormulaCallGroup callGroup = callGroupDAO.readFormulaCallGroup(callGroupName);
        return resolveDependencies(callGroup.getCalls());
    }

    private ResolveResult resolveDependencies(List<FormulaCall> calls) {
        Map<FormulaCall, Set<String>> inputFields = new HashMap<>();
        Map<FormulaCall, Set<String>> outputFields = new HashMap<>();
        for (FormulaCall call : calls) {
            getFormulaDependencies(call, inputFields, outputFields);
        }
        return topologicalSort(inputFields, outputFields);
    }

    private ResolveResult topologicalSort(Map<FormulaCall, Set<String>> input, Map<FormulaCall, Set<String>> output) {
        DirectedAcyclicGraph<FormulaCall, String> dag = new DirectedAcyclicGraph(String.class);
        for (FormulaCall outer : output.keySet()) {
            dag.addVertex(outer);
            for (FormulaCall inner : input.keySet()) {
                dag.addVertex(inner);
                Set<String> inputFields = new HashSet<>(input.get(inner));
                inputFields.retainAll(output.get(outer));
                if (!inputFields.isEmpty()) {
                    // there is intersect between input and output
                    inputFields.forEach(field -> {
                        dag.addEdge(outer, inner, getEdge(outer, inner, field));
                        log.info("From " + outer.getFormulaName() + " to " + inner.getFormulaName() + " via " + field);
                    });
                }
            }
        }
        List<FormulaCall> list = new ArrayList<>();
        for (FormulaCall formulaCall : dag) {
            list.add(formulaCall);
        }
        return new ResolveResult(list, input);
    }

    private String getEdge(FormulaCall from, FormulaCall to, String field) {
        return from.getFormulaName() + "-" + field + "-" + to.getFormulaName();
    }

    private void getFormulaDependencies(FormulaCall call, Map<FormulaCall, Set<String>> inputFields, Map<FormulaCall, Set<String>> outputFields) {
        String name = call.getFormulaName();
        CompiledFormula compiledFormula = formulaDAO.readFormula(name);
        Set<String> inputSet = new HashSet<>(compiledFormula.getReferenceFields());
        Set<String> outputSet = getOutputFields(call);
        // remove the self-generated fields
        inputSet.removeAll(outputSet);
        inputFields.put(call, inputSet);
        outputFields.put(call, outputSet);
    }

    private Set<String> getOutputFields(FormulaCall call) {
        List<FormulaOutput> outputs = call.getOutputMapping();
        return outputs.stream().map(FormulaOutput::getFieldName).collect(Collectors.toSet());
    }
}
