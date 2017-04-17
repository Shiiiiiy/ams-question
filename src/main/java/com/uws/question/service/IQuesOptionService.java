package com.uws.question.service;

import java.util.List;

import com.uws.domain.question.QuestionOptionModel;

public abstract interface IQuesOptionService {

	/**
	 * 根据题目id查询相关的答案选项
	 * @param itemId
	 * @return
	 */
	List<QuestionOptionModel> queryOptionByItemId(String itemId);
	
	/**
	 * 根据题目id删除相关的答案选项
	 * @param itemId
	 */
	void deleteOptionByItemId(String itemId);
	
	/**
	 * 保存答案选项实体
	 * @param option
	 */
	void saveOption(QuestionOptionModel option);
	
	/**
	 * 更新答案选项实体
	 * @param option
	 */
	void updateOption(QuestionOptionModel option);
	
	/**
	 * 根据id查找答案选项信息
	 * @param id
	 * @return
	 */
	QuestionOptionModel findOptionById(String id);
	
	/**
	 * 根据id删除答案选项实体
	 * @param id
	 */
	void deleteOptionById(String id);

	/**
	 * 根据选项名称和题目Id查询选项信息
	 * @param itemId
	 * @param optionName
	 * @return
	 */
	List<QuestionOptionModel> queryOptionByNameAndItemId(String itemId,
			String optionName);
	
}
