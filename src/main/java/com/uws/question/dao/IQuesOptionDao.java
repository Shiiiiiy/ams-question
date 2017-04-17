package com.uws.question.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.domain.question.QuestionOptionModel;

/**
* @ClassName: IQuesOptionDao 
* @Description: 答案选项管理 DAO 接口
 */
public abstract interface IQuesOptionDao extends IBaseDao{

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
	 * 根据选项名称和题目Id查询选项信息
	 * @param itemId
	 * @param optionName
	 * @return
	 */
	List<QuestionOptionModel> queryOptionByNameAndItemId(String itemId,
			String optionName);
	
}
