package com.uws.question.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.question.QuestionAnswerBaseModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionOptionModel;
import com.uws.domain.teacher.TeacherInfoModel;
import com.uws.question.dao.IQuesStatisticDao;
import com.uws.common.util.Constants;
import com.uws.common.util.QuestionNaireConstants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.impl.DicFactory;

/**
 * 问卷统计DAO实现
 */
@Repository("quesStatisticDao")
@SuppressWarnings("all")
public class QuesStatisticDaoImpl extends BaseDaoImpl implements
		IQuesStatisticDao {

	public Page queryQuesInfo(Integer pageSize, Integer pageNo,
			QuestionInfoModel question) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(
				" select distinct qaw.questionInfo.id from QuestionAnswerBaseModel qaw where qaw.answerStatus.id = ? ");
		values.add(QuestionNaireConstants.ANSWER_STATUS_COMMITED.getId());

		// 问卷名称
		if (!StringUtils.isEmpty(question.getName())) {
			hql.append(" and qaw.questionInfo.name like ? ");
			values.add("%" + question.getName() + "%");
		}

		// 问卷类型
		if (null != question.getTypeDic()
				&& StringUtils.isNotEmpty(question.getTypeDic().getId())) {
			hql.append(" and qaw.questionInfo.typeDic.id=? ");
			values.add(question.getTypeDic().getId());
		}

		// 创建日期_开始时间
		if (!StringUtils.isEmpty(question.getBeginDate())) {
			hql.append(" and to_char(qaw.questionInfo.createTime ,'yyyy-MM-dd') >= ? ");
			values.add(question.getBeginDate());
		}

		// 创建日期_结束时间
		if (!StringUtils.isEmpty(question.getStopDate())) {
			hql.append(" and to_char(qaw.questionInfo.createTime ,'yyyy-MM-dd') <= ? ");
			values.add(question.getStopDate());
		}

		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize,
					values.toArray());
	}

	@Override
	public long countQuesAnswer(String questionId, Dic status) {
		StringBuffer hql = new StringBuffer(
				" select count(respondent.id) from QuestionAnswerBaseModel ");
		hql.append(" where quesnaireType.id= '" + status.getId() + "'");
		hql.append(" and questionInfo.id='" + questionId + "'");
		hql.append(" group by questionInfo.id");
		return ((Long) queryUnique(hql.toString(), new Object[0])).longValue();
	}

	@Override
	public List<QuestionOptionModel> queryOptionList(String questionId,
			String itemId) {
		StringBuffer hql = new StringBuffer(
				" select qiom.itemOption from QuestionItemOptionModel qiom ");
		hql.append(" where qiom.questionNaire.id= '" + questionId + "'");
		hql.append(" and qiom.paperItemId in("
				+ " select infoItem.id from QuestionInfoItemModel infoItem where infoItem.questionItem.id='"
				+ itemId + "')");
		// hql.append(" and qiom.delStatusDic.id= '"+Constants.STATUS_NORMAL.getId()+"'");
		return this.query(hql.toString());
	}

	@Override
	public long countItemAnswer(String questionId, String itemId,
			String optionId, Dic itemType) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(
				" select count(qsm.answer.id) from QuesStatisticModel qsm where 1=1 ");
		hql.append(" and qsm.question.id= '" + questionId + "'");
		hql.append(" and qsm.item.id= '" + itemId + "'");
		if (null != itemType && StringUtils.isNotEmpty(itemType.getId())) {
			hql.append(" and qsm.itemType.id= '" + itemType.getId() + "'");
		}
		hql.append(" and qsm.id= '" + optionId + "'");
		return ((Long) queryUnique(hql.toString(), new Object[0])).longValue();
	}

	@Override
	public Page queryStudentsByQuestion(Integer pageSize, Integer pageNo,
			StudentInfoModel stu, String queid) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(
				" select s.NAME as name,d.NAME as genderName,s.STU_NUMBER as stuNumber,c.CLASS_NAME as className,m.MAJOR_NAME as majorName,c1.NAME as orgName,s.ID ");
		hql.append("from HKY_STUDENT_INFO s left join HKY_QUESTION_ANSWER_BASEINFO qa on s.ID = qa.RESPONDENT ");
		hql.append("left join DIC d on d.ID=s.SEX ");
		hql.append("left join HKY_BASE_CLASS c on c.ID=s.CLASS_ID ");
		hql.append("left join HKY_BASE_MAJOR m on m.ID=s.MAJOR ");
		hql.append("left join HKY_BASE_COLLAGE c1 on c1.ID=s.COLLEGE ");
		hql.append("where qa.ANSWER_STATUS = ? ");
		values.add(QuestionNaireConstants.ANSWER_STATUS_COMMITED.getId());
		if (DataUtil.isNotNull(stu.getName())) {
			hql.append(" and s.NAME like ? ");
			values.add("%" + stu.getName() + "%");
		}
		if (DataUtil.isNotNull(stu.getStuNumber())) {
			hql.append(" and s.STU_NUMBER like ? ");
			values.add("%" + stu.getStuNumber() + "%");
		}

		hql.append(" and qa.QUESNAIRE_ID = ? ");
		values.add(queid);

		hql.append(" order by s.ID");

		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize,
				values.toArray());
	}

	@Override
	public Page queryTeachersByQuestion(Integer pageSize, Integer pageNo,
			BaseTeacherModel tea, String queid) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(
				" select t.NAME,d.NAME,t.CODE,c.NAME as orgName ");
		hql.append("from HKY_BASE_TEACHER t left join HKY_QUESTION_ANSWER_BASEINFO qa on t.ID = qa.RESPONDENT ");
		hql.append("left join DIC d on d.ID=t.GENDER ");
		hql.append("left join HKY_BASE_COLLAGE c on c.ID=t.ORG ");
		hql.append("where qa.ANSWER_STATUS = ? ");
		values.add(QuestionNaireConstants.ANSWER_STATUS_COMMITED.getId());

		if (DataUtil.isNotNull(tea.getName())) {
			hql.append(" and t.NAME like ? ");
			values.add("%" + tea.getName() + "%");
		}
		if (DataUtil.isNotNull(tea.getCode())) {
			hql.append(" and t.CODE like ? ");
			values.add("%" + tea.getCode() + "%");
		}

		hql.append(" and qa.QUESNAIRE_ID = ? ");
		values.add(queid);

		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize,
				values.toArray());
	}

	/**
	 * 获取问卷所有的答案信息
	 * 
	 * @Description: TODO
	 * @author: 唐靖
	 * @date: 2016-11-30 下午3:32:15
	 * @param queid
	 * @return
	 */
	public List<Object> queryQuestionAnswerByQuestionId(String queid) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(
				"select s.NAME as name,d.NAME as genderName,s.STU_NUMBER as stuNumber,c.CLASS_NAME as className,m.MAJOR_NAME as majorName,c1.NAME as orgName,s.ID from HKY_STUDENT_INFO s left join HKY_QUESTION_ANSWER_BASEINFO qa on s.ID = qa.RESPONDENT left join DIC d on d.ID=s.SEX left join HKY_BASE_CLASS c on c.ID=s.CLASS_ID left join HKY_BASE_MAJOR m on m.ID=s.MAJOR left join HKY_BASE_COLLAGE c1 on c1.ID=s.COLLEGE where qa.ANSWER_STATUS = ?  and qa.QUESNAIRE_ID = ?  order by s.ID");

		return querySQL(hql.toString(),
				QuestionNaireConstants.ANSWER_STATUS_COMMITED.getId(), queid);
	}
}
