package com.uws.question.util;

import java.util.List;

import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;


public class Constants extends com.uws.common.util.Constants{
	
	/**
	 * 调查问卷—题目管理模块
	 */
	public static final String QUESTION_QUESTIONITEM = "/question/itemManage";
	
	/**
	 * 调查问卷—题目管理模块
	 */
	public static final String QUESTION_MANAGE = "/question/paperManage";
	
	/**
	 * 调查问卷—问卷统计模块
	 */
	public static final String QUESTION_STATISTIC = "/question/statisticManage";
	
	/**
	 * 调查问卷—问卷管理
	 */
	public static final String QUESTIONNAIRE_STATISTIC = "/question/quesManage";
	
	/**
	 * 调查问卷—问卷管理
	 */
	public static final String QUESTIONNAIRE_WRITE = "/question/quesWrite";
	
	/**
	 * 默认分页大小
	 */
	public static int DEFALT_PAGE_SIZE=2;
	
	/**
	 * 数据字典工具类
	 */
	private static DicUtil dicUtil=DicFactory.getDicUtil();
	
	/**
	 * 【系统数据字典_问卷状态_保存】
	 */
	public static final Dic PAPER_STATUS_SAVED=dicUtil.getDicInfo("PAPER_STATUS", "STATUS_SAVE");
	
	/**
	 * 【系统数据字典_问卷状态_启用】
	 */
	public static final Dic PAPER_STATUS_ENABLE=dicUtil.getDicInfo("PAPER_STATUS", "STATUS_ENABLE");
	
	/**
	 * 【系统数据字典_问卷状态_禁用】
	 */
	public static final Dic PAPER_STATUS_DISABLE=dicUtil.getDicInfo("PAPER_STATUS", "STATUS_DISABLE");
	
	/**
	 * 【系统数据字典_题型_简答】
	 */
	public static  Dic ITEMTYPE_ESSAY= dicUtil.getDicInfo("ITME_TYPE", "ESSAY_QUESTION");
	
	/**
	 *	问卷状态
	 */
	public static enum PAPER_STATUS{
		/**
		 * 保存状态
		 */
		STATUS_SAVE,
		/**
		 * 启用状态
		 */
		STATUS_ENABLE,
		/**
		 * 禁用状态
		 */
		STATUS_DISABLE
	}
	
	/**
	 * 问卷类型
	 */
	public static List<Dic> paperTypeList = dicUtil.getDicInfoList("QUESINFO_TYPE");
	
	/**
	 * 【系统数据字典_问卷类型_迎新】
	 */
	public static final Dic QUESINFO_TYPE_ORIENTATION=dicUtil.getDicInfo("QUESINFO_TYPE", "QUES_ORIENTATION");
	
	/**
	 * 【系统数据字典_问卷类型_毕业】
	 */
	public static final Dic QUESINFO_TYPE_GRADUATION=dicUtil.getDicInfo("QUESINFO_TYPE", "QUES_GRADUATION");
	
	/**
	 * 【系统数据字典_问卷类型_公共】
	 */
	public static final Dic QUESINFO_TYPE_COMMON=dicUtil.getDicInfo("QUESINFO_TYPE", "QUES_COMMON");
	
	/**
	 * 问卷状态
	 */
	public static List<Dic> paperStatusList = dicUtil.getDicInfoList("PAPER_STATUS");
	
	/**
	 * 答题状态
	 */
	public static  List<Dic> answerStatusList = dicUtil.getDicInfoList("ANSWER_STATUS");
	
	/**
	 * 题型
	 */
	public static  List<Dic> itemTypeList = dicUtil.getDicInfoList("ITME_TYPE");
	
	/**
	 * 页面控件选中标志
	 */
	public static enum INPUT_FLAG_ENUMS{
		/**
		 * 单选，复选选中标志
		 */
		CHECKED,
		/**
		 * 下拉列表选择状态
		 */
		SELECTED,
		/**
		 * 选中
		 */
		TRUE,
		/**
		 * 不选中
		 */
		FALSE
	}
}
