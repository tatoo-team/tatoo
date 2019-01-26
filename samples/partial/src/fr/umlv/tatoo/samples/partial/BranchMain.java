package fr.umlv.tatoo.samples.partial;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.tools.SemanticStack;

public class BranchMain {
	public static void main(String[] args) throws FileNotFoundException {
		Reader reader;
		if (args.length==0)
			reader=new InputStreamReader(System.in);
		else
			reader=new FileReader(args[0]);
		ReaderWrapper tokenBuffer=new ReaderWrapper(reader,
				new LocationTracker());

		// parsers share the same stack
		SemanticStack stack=new SemanticStack();

		BranchTerminalEvaluator terminalEvaluator=
			new BranchTerminalEvaluator();
		BranchGrammarEvaluator grammarEvaluator=
			new BranchGrammarEvaluator();

		fr.umlv.tatoo.samples.partial.calc.tools.Analyzers.analyzerTokenBufferBuilder(tokenBuffer,
				terminalEvaluator,
				grammarEvaluator,
				stack).trace().expert().
				branches().

				link(fr.umlv.tatoo.samples.partial.calc.parser.TerminalEnum.expr,
				    fr.umlv.tatoo.samples.partial.expr.tools.Analyzers.analyzerTokenBufferBuilder(tokenBuffer,
								terminalEvaluator, grammarEvaluator,
								stack).trace().expert().
								branches()

				).createAnalyzer().getLexer().run();
	}

}
