package edu.ucr.cs.riple.autofixer.explorers;

import edu.ucr.cs.riple.autofixer.AutoFixer;
import edu.ucr.cs.riple.autofixer.Report;
import edu.ucr.cs.riple.autofixer.metadata.index.Bank;
import edu.ucr.cs.riple.autofixer.metadata.index.Error;
import edu.ucr.cs.riple.autofixer.metadata.index.FixEntity;
import edu.ucr.cs.riple.injector.Fix;

import java.util.Set;

public class DummyExplorer extends Explorer{

    public DummyExplorer(AutoFixer autoFixer, Bank<Error> errorBank, Bank<FixEntity> fixBank) {
        super(autoFixer, errorBank, fixBank);
    }

    @Override
    public Report effect(Fix fix) {
        return new Report(fix, -1);
    }

    @Override
    public Report effectByScope(Fix fix, Set<String> workSet) {
        return new Report(fix, -1);
    }

    @Override
    public boolean isApplicable(Fix fix) {
        return true;
    }

    @Override
    public boolean requiresInjection(Fix fix) {
        return false;
    }
}
