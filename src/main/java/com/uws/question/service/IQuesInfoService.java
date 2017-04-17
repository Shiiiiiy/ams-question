package com.uws.question.service;

import java.util.List;

import com.uws.core.base.BaseModel;
import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionItemOptionModel;

/**
 * 调查问卷维护service接口
 */
public interface IQuesInfoService extends IBaseService {
	
	/**
	 * 查询调查问卷信息
	 * @param questionInfo			问卷实体对象
	 * @param pageNum					分页页码
	 * @param pageSize					分页大小
	 * @return										问卷分页集合
	 */
	public Page queryQuesInfo(QuestionInfoModel questionInfo,int pageNum,int pageSize);
	
	/**
	 * 查询调查问卷信息
	 * @param pageNum					分页页码
	 * @param pageSize					分页大小
	 * @param currentUserId		当前用户id
	 * @return										问卷分页集合
	 */
	public Page queryUserQuesInfo(int pageNum,int pageSize,String currentUserId);
	
	/**
	 * 获取授权学生的调查问卷列表
	 * @param condition					查询条件【学生 ids】
	 * @param pageNo						每页大小
	 * @param pageSize					分页大小
	 * @return										分页查询的结果
	 */
	public Page queryStuQuesInfo(String condition,int pageNo, int pageSize);

	/**
	 * 获取授权教师的调查问卷列表
	 * @param condition					查询条件【教师 ids】
	 * @param pageNo						每页大小
	 * @param pageSize					分页大小
	 * @return										分页查询的结果
	 */
	public Page queryTeacherQuesInfo(String teacherIdsConditon, int pageNo,int pageSize);
	
	/**
	 * 根据主键查询问卷信息
	 * @param id									问卷主键
	 * @return										问卷对象
	 */
	public QuestionInfoModel getQuesInfoById(String id);
	
	/**
	 * 获取单个未被逻辑删除的问卷
	 * @param questionNaireId
	 * @return
	 */
	public QuestionInfoModel getQuestionNairePo(String questionNaireId);
	
	/**
	 * 查询问卷下的问题列表
	 * @param paperId					问卷主键
	 * @return										问卷-问题【关系实体】列表
	 */
	public List<QuestionInfoItemModel> getQuesItemByInfoId(String paperId);

	/**
	 * 统计问卷选定的题目
	 * @param paperId					问卷主键
	 * @return										问卷统计集合
	 */
	public List<QuestionInfoItemModel> getItemByPaperId4Summery(String paperId);

	/**
	 * 根据问卷ID获取问卷-试题对象
	 * @param pk			主键
	 * @return
	 */
	public QuestionInfoItemModel getQuesInfoItemByPK(String pk);
	
	/**
	 * 根据问卷名称查询问卷列表
	 * @param name							问卷名称
	 * @return										问卷名称列表
	 */
	public List<QuestionInfoModel> getQuesInfoByInfoName(String name);
	
	/**
	 * 物理删除问卷信息
	 * @param id									问卷主键
	 */
	public void deletePaperById(String id);
	
	/**
	 * 保存问卷信息
	 * @param qim		新增的问卷对象
	 */
	public void saveQuesInfo(BaseModel qim);
	
	/**
	 * 修改问卷信息
	 * @param quesInfoModel		待修改文件对象
	 */
	public void updateQuesInfo(QuestionInfoModel quesInfoModel);

	/**
	 * 保存问卷-试题信息
	 * @param bm				问卷-试题对象
	 */
	public void savePaperItem(QuestionInfoItemModel bm);

	/**
	 * 获取问卷中的某个问题的关系对象
	 * @param paperId	问卷ID
	 * @param itemId		问题ID
	 * @return						问卷-试题对象
	 */
	public QuestionInfoItemModel querySinglePaperItem(String paperId,String itemId);

	/**
	 * 物理删除问卷编辑中的单个问题
	 * @param paperId	问卷ID
	 * @param itemId		问题ID
	 */
	public void deletePaperItem(String paperId, String itemId);
	
	/**
	 * 物理删除问卷编辑中的所有问题
	 * @param paperId	问卷ID
	 */
	public void deletePaperItem(String paperId);

	/**
	 * 仅仅删除问卷下的题目、选项
	 * @param paperId
	 */
	public void deletePaperItemOnly(String paperId);
	
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
	 * 级联删除问题选项
	 * @param itemId
	 */
	public void deleteItemOption(String itemId);

	/**
	 * 获取【问题-选项】关系表中，问题对象的选项
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
	 * 保存对象
	 * @param bm
	 */
	public void saveBm(BaseModel bm);

	/**
	 * 根据问卷ID清空问卷-答题人列表
	 * @param paperId		问卷ID
	 * @param userType		答题人类型
	 */
	public void deleteQnrmInfo(String paperId,String userType);

	/**
	 * 获取问卷的学生授权用户
	 * @param paperId	问卷ID
	 * @param pageNo		当前页号
	 * @param pageSize	分页大小
	 * @return						问卷授权用户分页大小
	 */
	public Page getPaperStudent(String paperId, int pageNo, int pageSize);

	/**
	 * 获取问卷的教师授权用户
	 * @param paperId	问卷ID
	 * @param pageNo		当前页号
	 * @param pageSize	分页大小
	 * @return						问卷授权用户分页大小
	 */
	public Page getPaperTeacher(String paperId, int i, int pageSize);

}
