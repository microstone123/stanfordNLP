package stanfordNLP;

import java.io.File;
import java.util.List;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.PartOfSpeechTagging;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stanfordNLP.questiontypeanalysis.patternbased.MainPartExtracter;
import stanfordNLP.util.Tools;

public class Test {
	private static final Logger LOG = LoggerFactory.getLogger(Test.class);

	static {
		String appPath = Tools.getAppPath(Test.class);
		String confFile = appPath + "/web/dic/word_v_1_3/word.local.conf";
		if (!new File(confFile).exists()) {
			confFile = appPath + "/jar/dic/word_v_1_3/word.local.conf";
		}
		if (new File(confFile).exists()) {
			LOG.info("word分词的自定义配置文件：" + confFile);
			WordConfTools.forceOverride(confFile);
		} else {
			LOG.info("不存在word分词的自定义配置文件：" + confFile);
		}
	}

	/**
	 * 带词性标注（包括细分词性标注）的分析方法
	 *
	 * @param str
	 *            需要分词的文本
	 * @return 分词结果
	 */
	public static List<Word> parseWithoutStopWords(String str) {
		List<Word> words = WordSegmenter.seg(str, SegmentationAlgorithm.MaxNgramScore);
		// 词性标注
		PartOfSpeechTagging.process(words);
		return words;
	}

	public static List<Word> parse(String str) {
		List<Word> words = WordSegmenter.segWithStopWords(str, SegmentationAlgorithm.MaxNgramScore);
		// 词性标注
		PartOfSpeechTagging.process(words);
		return words;
	}

	private static final MainPartExtracter mainPartExtracter = new MainPartExtracter();

	public static void main(String[] args) {
		List<Word> parse;
		String question ;
//		question ="毛绒玩具给我洗下";
//		question ="我要使用高温自洁程序";
		question = "我有几件的确良衣服要洗";

		parse = parse(question);
		setParse(parse, question);
//		question = "涤粘华达呢怎么洗";
//		parse = parse(question);
//		setParse(parse, question);
	}

	private static void setParse(List<Word> parse, String question) {
		System.out.println("问题：" + question);
		System.out.println("词和词性序列：" + parse);
		StringBuilder termWithNatureStrs = new StringBuilder();
		int i = 0;
		StringBuilder natureStrs = new StringBuilder();
		for (Word word : parse) {
			termWithNatureStrs.append(word.getText()).append("/").append(word.getPartOfSpeech().getPos()).append(" ");
			if ((i++) > 0) {
				natureStrs.append("/");
			}
			natureStrs.append(word.getPartOfSpeech().getPos());
		}
		System.out.println("词性序列：" + natureStrs.toString());
		StringBuilder wordStr = new StringBuilder();
		for (Word word : parse) {
			wordStr.append(word.getText()).append("/");
		}
		LOG.info("分词结果为：" + wordStr.toString().trim());
		mainPartExtracter.getMainPart(question);
	}
}
