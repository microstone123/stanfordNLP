/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package stanfordNLP.parser;

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

/**
 * 分词器
 *
 * @author 杨尚川
 */
public class WordParser {

	private static final Logger LOG = LoggerFactory.getLogger(WordParser.class);

	static {
		String appPath = Tools.getAppPath(WordParser.class);
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
		String question ="布什是个什么样的人";
		// List<Word> parse =
		// parse("在河边一排排梨树下面有许多的非洲象和熊猫，还有很多的桉树，红色的金鱼在水里游来游去，猎豹在绿色的草地上跑来跑去!");
		// System.out.println("一：" + parse);
		List<Word> parse = parse(question);
		System.out.println("问题："+question);
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