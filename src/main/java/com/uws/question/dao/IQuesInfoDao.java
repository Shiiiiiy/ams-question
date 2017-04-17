package com.uws.question.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionItemOptionModel;

/**
 * 调查问卷维护Dao
 */
public interface IQuesInfoDao extends IBaseDao {
	
	/**
	 * 查询调查问卷信息
	 * @param questionInfo
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Page queryQuesInfo(QuestionInfoModel questionInfo,int pageNum,int pageSize);
	
	/**
	 * 查询调查问卷信息
	 * @param pageNum
	 * @param pageSize
	 * @param currentUserId		当前用户id
	 * @return
	 */
	public Page queryUserQuesInfo(int pageNum,int pageSize,String currentUserId);
	
	/**
	 * 获取单个问卷实体类
	 * @param questionNaireId
	 * @return
	 */
	public QuestionInfoModel getQuestionNairePo(String questionNaireId);
	
	/**
	 * 通过问卷id查询问题列表
	 * @param paperId
	 * @return
	 */
	public List<QuestionInfoItemModel> getQuesItemByInfoId(String paperId);
	
	/**
	 * 统计问卷选定的题目
	 * @param paperId
	 * @return
	 */
	public List<QuestionInfoItemModel> getItemByPaperId4Summery(String paperId);
	
	/**
	 * 根据问卷ID获取问卷-试题对象
	 * @param pk		主键
	 * @return
	 */
	public QuestionInfoItemModel getQuesInfoItemByPK(String pk);
	
	/**
	 * 通过问卷名称查询问卷
	 * @param name
	 * @return
	 */
	public List<QuestionInfoModel> getQuesInfoByInfoName(String name);
	
	/**
	 * 通过ID删除问卷，物理删除
	 * @param paperId
	 */
	public void deletePaperById(String paperId);

	/**
	 * 获取单个问卷-问题对象
	 * @param paperId
	 * @param itemId
	 * @return
	 */
	public QuestionInfoItemModel querySinglePaperItem(String paperId,String itemId);

	/**
	 * 物理删除问卷编辑中的单个问题
	 * @param paperId
	 * @param itemId
	 */
	public void deletePaperItem(String paperId, String itemId);
	
	/**
	 * 物理删除问卷编辑中的所有问题
	 * @param paperId
	 */
	public void deletePaperItem(String paperId);
	
	/**
	 * 废弃问卷
	 * @param paperId	问卷ID
	 */
	public void abandonPaper(String paperId);

	/**
	 * 废弃问卷下的问题
	 * @param paperId	问卷ID
	 */
	public void abandonPaperItem(String paperId);

	/**
	 * 删除【问题-选项】关系表中的选项
	 * @param itemId
	 */
	public void deleteItemOption(String itemId);

	/**
	 * 废弃【问题-选项】关系表中：问题下的选项
	 * @param itemId
	 */
	public void abandonItemOption(String itemId);

	/**
	 * 查询【问题-选项】关系表中:
	 * 对应问题下的某个选项
	 * @param questionNaireId	问卷id
	 * @param itemId						问题id
	 * @param itemOptionId									
	 * @return
	 */
	public QuestionItemOptionModel getItemOption(String paperItemId, String itemOptionId);

	/**
	 * 获取【问卷-问题】关系表中，问题下的选项
	 * @param qiim
	 * @return
	 */
	public List<QuestionItemOptionModel> getItemOptioinList(QuestionInfoItemModel qiim);

	/**
	 * 查询问卷下对应类型的题目
	 * @param paperId				问卷ID
	 * @param paperType			问卷类型
	 * @return									问卷题目列表
	 */
	public List<QuestionInfoItemModel> getItemByPaperType(String paperId,String paperType);

	/**
	 * 根据问卷ID清空问卷-答题人列表
	 * @param paperId				问卷ID
	 * @param userType				答题人类型
	 */
	public void deleteQnrmInfo(String paperId,String userType);

	/**
	 * 获取授权学生的调查问卷列表
	 * @param condition					查询条件【学生 ids】
	 * @param pageNo						每页大小
	 * @param pageSize					分页大小
	 * @return										分页查询的结果
	 */
	public Page queryStuQuesInfo(String condition, int pageNo, int pageSize);

	/**
	 * 获取授权教师的调查问卷列表
	 * @param condition					查询条件【教师 ids】
	 * @param pageNo						每页大小
	 * @param pageSize					分页大小
	 * @return										分页查询的结果
	 */
	public Page queryTeacherQuesInfo(String condition, int pageNo, int pageSize);
	
	/**
	 * 分页获取问卷学生用户列表
	 * @param paperId					问卷ID
	 * @param pageNo						每页大小
	 * @param pageSize					分页大小
	 * @return										分页查询的结果
	 */
	public Page getPaperStudent(String paperId, int pageNo, int pageSize);

	/**
	 * 分页获取问卷教师用户列表
	 * @param paperId					问卷ID
	 * @param pageNo						每页大小
	 * @param pageSize					分页大小
	 * @return										分页查询的结果
	 */
	public Page getPaperTeacher(String paperId, int pageNo, int pageSize);
}
