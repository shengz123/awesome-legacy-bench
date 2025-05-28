package com.alphaentropy.factor.core.infrastructure.dao;

import com.alphaentropy.factor.core.domain.CompiledFormula;

import java.util.List;

public interface FormulaDAO {
    void writeFormula(CompiledFormula compiledFormula);

    CompiledFormula readFormula(String formulaName);

    List<CompiledFormula> loadAll();

    boolean deleteFormula(String name);
}
