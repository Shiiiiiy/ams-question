package com.uws.question.service;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionItemModel;

public abstract interface IQuesItemService {

	/**
	 * 问卷问题—分页查询
	 * 
	 * @param pageSize
	 * @param pageNo
	 * @param quesItem
	 * @return
	 */
	public Page queryQuesItem(Integer pageSize, Integer pageNo,
			QuestionItemModel quesItem);

	/**
	 * 题目管理—根据名称和id查询题目信息
	 * 
	 * @param itemName
	 * @param id
	 * @param itemTypeId
	 * @param itemCategoryId
	 * @return
	 */
	public List<QuestionItemModel> queryQuesItemByName(String itemName,
			String id, String itemCategoryId, String itemTypeId);

	/**
	 * 保存题目信息
	 * 
	 * @param quesItem
	 */
	public void saveQuesItem(QuestionItemModel quesItem);

	/**
	 * 更新题目组信息
	 * 
	 * @param quesItem
	 */
	public void updateQuesItem(QuestionItemModel quesItem);

	/**
	 * 根据id查找题目信息
	 * 
	 * @param id
	 * @return
	 */
	public QuestionItemModel findItemById(String id);

	/**
	 * 根据id删除对应的题目
	 * 
	 * @param id
	 */
	public void delItemById(String id);

	/**
	 * 根据姓名、题型、题目类别，获取题目列表
	 * 
	 * @param i
	 * @param pageNo
	 * @param itemName
	 * @param itemTypeId
	 * @param itemCategoryId
	 * @return
	 */
	public Page queryItemCompList(int i, int pageNo, String itemName,
			String itemTypeId, String itemCategoryId, String paperType);

	/**
	 * 根据题目ID获取题目
	 * 
	 * @param itemId
	 *            题目ID
	 * @return 题目对象
	 */
	public QuestionItemModel queryItemById(String itemId);

}
