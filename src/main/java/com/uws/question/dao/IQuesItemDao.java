package com.uws.question.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionItemModel;

/**
 * @ClassName: IQuesItemDao
 * @Description: 题目管理 DAO 接口
 */
public abstract interface IQuesItemDao extends IBaseDao {

	/**
	 * 题目管理—分页查询
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
	 * @return
	 */
	public List<QuestionItemModel> queryQuesItemByName(String itemName,
			String id, String itemCategoryId, String itemTypeId);

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

	public QuestionItemModel queryItemById(String itemId);

}
