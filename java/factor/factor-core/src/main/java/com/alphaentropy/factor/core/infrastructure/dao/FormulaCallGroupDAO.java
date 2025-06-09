package com.alphaentropy.factor.core.infrastructure.dao;

import com.alphaentropy.factor.core.domain.FormulaCallGroup;

import java.util.List;

public interface FormulaCallGroupDAO {
    void writeFormulaCalls(FormulaCallGroup formulaCallGroup);

    FormulaCallGroup readFormulaCallGroup(String label);

    List<FormulaCallGroup> loadAll();

    boolean deleteFormulaCalls(String name);
}
