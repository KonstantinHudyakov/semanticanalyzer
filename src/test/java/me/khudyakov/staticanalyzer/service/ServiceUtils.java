package me.khudyakov.staticanalyzer.service;

import me.khudyakov.staticanalyzer.entity.syntaxtree.SyntaxTree;
import me.khudyakov.staticanalyzer.entity.ProgramCode;
import me.khudyakov.staticanalyzer.util.SyntaxAnalyzerException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceUtils {

    static final CodeParser codeParser = new CodeParserImpl();
    static final SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzerImpl();
    static final FeatureFinder framingIfFinder = new FramingIfTwoStepFinder();

    static SyntaxTree parseAndAnalyze(String code) throws SyntaxAnalyzerException, ParseException {
        ProgramCode programCode = codeParser.parse(code);
        return syntaxAnalyzer.createSyntaxTree(programCode);
    }

    static SyntaxTree addChange(SyntaxTreeChangesCache cache, String code) throws ParseException, SyntaxAnalyzerException {
        SyntaxTree syntaxTree = parseAndAnalyze(code);
        cache.addNewChange(syntaxTree);

        return syntaxTree;
    }

    static List<SyntaxTree> addChangeSequence(SyntaxTreeChangesCache cache, String... codes) throws SyntaxAnalyzerException, ParseException {
        List<SyntaxTree> list = new ArrayList<>();
        for(String code : codes) {
            list.add(addChange(cache, code));
        }

        return list;
    }

    static void analyzeOrThrow(String... codes) throws SyntaxAnalyzerException, ParseException {
        for (String code : codes) {
            parseAndAnalyze(code);
        }
    }

    // this method may be used with another argument in future
    static void analyzeAndCatchExceptions(Class<? extends Throwable> expectedException, String... codes) {
        Arrays.stream(codes)
              .forEach(code -> assertThrows(expectedException, () -> parseAndAnalyze(code)));
    }
}
