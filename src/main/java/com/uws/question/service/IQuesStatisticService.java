package com.uws.question.service;

import java.util.List;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.question.QuestionAnswerBaseModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionOptionModel;
import com.uws.sys.model.Dic;

/**
 * 问卷统计service接口
 */
public interface IQuesStatisticService extends IBaseService {

	/**
	 * 问卷统计—查询所有问卷（已提交）
	 * 
	 * @param pageSize
	 * @param pageNo
	 * @param question
	 *            问卷信息
	 * @return
	 */
	Page queryQuesInfo(Integer pageSize, Integer pageNo,
			QuestionInfoModel question);

	/**
	 * 统计回答问卷的人数
	 * 
	 * @param questionId
	 *            问卷id
	 * @param dic
	 * @return
	 */
	long countQuesAnswer(String questionId, Dic dic);

	/**
	 * 统计某个答案选项的人数
	 * 
	 * @param questionId
	 *            问卷id
	 * @param itemId
	 *            题目id
	 * @param optionId
	 *            选项id
	 * @param itemType
	 *            题目类型
	 * @return
	 */
	long countItemAnswer(String questionId, String itemId, String optionId,
			Dic itemType);

	/**
	 * 根据问卷Id和题目Id查询对应的答案选项List
	 * 
	 * @param questionId
	 * @param itemId
	 * @return
	 */
	List<QuestionOptionModel> queryOptionList(String questionId, String itemId);

	/**
	 * 查询某个问卷已答题的人员信息(学生)
	 */
	Page queryStudentsByQuestion(Integer pageSize, Integer pageNo,
			StudentInfoModel stu, String queid);

	/**
	 * 查询某个问卷已答题的人员信息(教师)
	 */
	Page queryTeachersByQuestion(Integer pageSize, Integer pageNo,
			BaseTeacherModel tea, String queid);

	/**
	 * 获取问卷的所有的答题人
	 * 
	 * @Description: TODO
	 * @author: 唐靖
	 * @date: 2016-11-30 下午3:31:00
	 * @param queid
	 * @return
	 */
	List<Object> queryQuestionAnswerByQuestionId(String queid);
}
