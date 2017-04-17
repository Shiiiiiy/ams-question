package com.uws.question.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IQuestionNaireService;
import com.uws.common.service.IRankService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.AmsDateUtil;
import com.uws.common.util.QuestionNaireConstants;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.core.util.IdUtil;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionItemModel;
import com.uws.domain.question.QuestionItemOptionModel;
import com.uws.domain.question.QuestionNaireRespondentModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.question.service.IQuesInfoService;
import com.uws.question.service.IQuesItemService;
import com.uws.question.service.IQuesStatisticService;
import com.uws.question.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;

/**
 * 调查问卷维护Controller
 */
@Controller
public class QuesInfoController extends BaseController {

	private Logger logger = new LoggerFactory(QuesInfoController.class);

	@Autowired
	private IRankService rankService;

	@Autowired
	private IQuesInfoService quesInfoService;

	@Autowired
	private IQuesItemService quesItemService;

	@Autowired
	private IStudentCommonService studentCommonServie;

	@Autowired
	private IBaseDataService baseDateService;

	@Autowired
	private IQuesStatisticService quesStatisticService;

	@Autowired
	private IDicService dicService;

	SessionUtil sessionUtil = SessionFactory
			.getSession(Constants.QUESTION_MANAGE);
	@Autowired
	private IQuestionNaireService questionNaireService;
	// 数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();

	/**
	 * 查询调查问卷信息
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param questionInfo
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-query/quesInfoQuery" })
	public String queryQuesInfo(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, QuestionInfoModel questionInfo) {
		int pageNo = request.getParameter("pageNo") != null ? Integer
				.parseInt(request.getParameter("pageNo")) : 1;
		Page page = this.quesInfoService.queryQuesInfo(questionInfo, pageNo,
				Page.DEFAULT_PAGE_SIZE);
		List<Dic> typeList = Constants.paperTypeList;// 问卷类型
		List<Dic> paperList = Constants.paperStatusList;// 问卷状态
		model.addAttribute("page", page);
		model.addAttribute("questionInfo", questionInfo);
		model.addAttribute("typeList", typeList);
		model.addAttribute("paperList", paperList);
		model.addAttribute("curpageNo", pageNo);

		return Constants.QUESTIONNAIRE_STATISTIC + "/quesInfoList";
	}

	/**
	 * 查询调查问卷信息
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param questionInfo
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC + "/nsm/getPaperList" })
	public String getPaperList(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, QuestionInfoModel questionInfo,
			String pageNumber) {
		int pageNo = (DataUtil.isNotNull(pageNumber) && !pageNumber
				.equalsIgnoreCase("undefined")) ? Integer.parseInt(pageNumber)
				: 1;
		Page page = this.quesInfoService.queryQuesInfo(questionInfo, pageNo,
				Page.DEFAULT_PAGE_SIZE);
		List<Dic> typeList = Constants.paperTypeList;// 问卷类型
		List<Dic> paperList = Constants.paperStatusList;// 问卷状态
		model.addAttribute("page", page);
		model.addAttribute("questionInfo", questionInfo);
		model.addAttribute("typeList", typeList);
		model.addAttribute("paperList", paperList);
		model.addAttribute("curpageNo", pageNo);

		return Constants.QUESTIONNAIRE_STATISTIC + "/questionNaireList";
	}

	/**
	 * 跳转到问卷编辑页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({
			Constants.QUESTIONNAIRE_STATISTIC + "/opt-add/quesInfoAdd",
			Constants.QUESTIONNAIRE_STATISTIC + "/opt-modify/quesInfoModify" })
	public String quesInfoModify(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String id) {
		QuestionInfoModel questionInfo = null;
		if (DataUtil.isNotNull(id)) {
			questionInfo = this.quesInfoService.getQuesInfoById(id);
		} else {
			questionInfo = new QuestionInfoModel();
			questionInfo.setId(IdUtil.getUUIDHEXStr());
		}
		List<QuestionInfoItemModel> questionItemList = quesInfoService
				.getQuesItemByInfoId(id);
		model.addAttribute("questionItemList", questionItemList);
		model.addAttribute("questionInfo", questionInfo);
		model.addAttribute("itemTypeList", Constants.itemTypeList);
		model.addAttribute("categoryList", Constants.paperTypeList);
		model.addAttribute("paperTypeList", Constants.paperTypeList);
		model.addAttribute("commonTypeId",
				Constants.QUESINFO_TYPE_COMMON.getId());
		model.addAttribute("page", this.getPaperStudent(id));
		model.addAttribute("teacherPage", this.getPaperTeacher(id));
		return Constants.QUESTIONNAIRE_STATISTIC + "/quesInfoEdit";
	}

	/**
	 * 获取问卷的学生授权列表
	 * 
	 * @param paperId
	 *            问卷ID
	 * @return 用户的分页信息
	 */
	private Page getPaperStudent(String paperId) {
		List<StudentInfoModel> stuList = new ArrayList<StudentInfoModel>();
		Page page = this.quesInfoService.getPaperStudent(paperId, 1,
				Constants.DEFALT_PAGE_SIZE);
		List<User> respondentList = (List<User>) page.getResult();
		for (User user : respondentList) {
			StudentInfoModel stuInfo = studentCommonServie
					.queryStudentById(user.getId());
			stuList.add(stuInfo);
		}
		page.setResult(stuList);
		return page;
	}

	/**
	 * 获取问卷的教师授权列表
	 * 
	 * @param paperId
	 *            问卷ID
	 * @return 用户的分页信息
	 */
	private Page getPaperTeacher(String paperId) {
		List<BaseTeacherModel> teacherList = new ArrayList<BaseTeacherModel>();
		Page page = this.quesInfoService.getPaperTeacher(paperId, 1,
				Constants.DEFALT_PAGE_SIZE);
		List<User> respondentList = (List<User>) page.getResult();
		for (User user : respondentList) {
			BaseTeacherModel teacherInfo = baseDateService.findTeacherById(user
					.getId());
			teacherList.add(teacherInfo);
		}
		page.setResult(teacherList);
		return page;
	}

	/**
	 * 保存试卷
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-add/savePaperInfo" })
	public String savePaperInfo(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, QuestionInfoModel questionInfo) {
		QuestionInfoModel existQim = this.quesInfoService
				.getQuesInfoById(questionInfo.getId());
		if (DataUtil.isNotNull(existQim)) {// 修改问卷

			QuestionInfoModel qim = this.formatePaperUpdate(existQim,
					questionInfo);
			qim.setStatusDic(Constants.PAPER_STATUS_SAVED);// 设置问卷状态-保存
			this.quesInfoService.updateQuesInfo(qim);
		} else {// 新增问卷

			QuestionInfoModel qim = this.formatePaperSave(questionInfo);
			qim.setStatusDic(Constants.PAPER_STATUS_SAVED);// 设置问卷状态-保存
			this.quesInfoService.saveQuesInfo(qim);
		}

		return "redirect:/question/quesManage/opt-query/quesInfoQuery.do";
	}

	/**
	 * 修改问卷
	 * 
	 * @param existQim
	 * @param questionInfo
	 * @return
	 */
	private QuestionInfoModel formatePaperUpdate(QuestionInfoModel existQim,
			QuestionInfoModel questionInfo) {
		existQim.setName(questionInfo.getName());// 设置问卷名称
		Dic typeDic = new Dic();
		typeDic.setId(questionInfo.getPaperCategory());// 设置问卷类型
		existQim.setTypeDic(typeDic);
		existQim.setComments(questionInfo.getComments());// 设置备注
		// 设置修改时间
		existQim.setUpdateTime(AmsDateUtil.toTime(AmsDateUtil.getCurTime()));
		// 设置修改人
		User creator = new User();
		creator.setId(this.sessionUtil.getCurrentUserId());
		existQim.setUpdater(creator);
		// 设置问卷是否公开
		existQim.setIsOpen(questionInfo.getIsOpen());
		return existQim;
	}

	/**
	 * 新增问卷
	 * 
	 * @param questionInfo
	 * @return
	 */
	private QuestionInfoModel formatePaperSave(QuestionInfoModel questionInfo) {
		QuestionInfoModel qim = new QuestionInfoModel();
		BeanUtils.copyProperties(questionInfo, qim);
		// 设置问卷类型
		Dic typeDic = new Dic();
		typeDic.setId(questionInfo.getPaperCategory());
		qim.setTypeDic(typeDic);
		// 设置创建、修改人
		User creator = new User();
		creator.setId(this.sessionUtil.getCurrentUserId());
		qim.setCreator(creator);// 设置创建人
		qim.setUpdater(creator);// 设置修改人
		// 设置创建时间
		qim.setCreateTime(AmsDateUtil.toTime(AmsDateUtil.getCurDate()));
		// 设置修改时间
		qim.setUpdateTime(AmsDateUtil.toTime(AmsDateUtil.getCurTime()));
		// 设置逻辑删除状态
		qim.setDelStatusDic(Constants.STATUS_NORMAL);
		// 设置问卷是否公开
		qim.setIsOpen(questionInfo.getIsOpen());

		return qim;
	}

	/**
	 * 启用问卷
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-add/startPaperInfo" })
	public String startPaperInfo(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, QuestionInfoModel questionInfo) {
		QuestionInfoModel existQim = this.quesInfoService
				.getQuesInfoById(questionInfo.getId());
		if (DataUtil.isNotNull(existQim)) {// 修改问卷

			QuestionInfoModel qim = this.formatePaperUpdate(existQim,
					questionInfo);
			qim.setStatusDic(Constants.PAPER_STATUS_ENABLE);// 设置问卷状态-启用
			qim.setReleaseDate(DateUtil.getCurDate());// 设置发布时间
			this.quesInfoService.updateQuesInfo(qim);
		} else {// 新增问卷

			QuestionInfoModel qim = this.formatePaperSave(questionInfo);
			qim.setStatusDic(Constants.PAPER_STATUS_ENABLE);// 设置问卷状态-启用
			qim.setReleaseDate(DateUtil.getCurDate());// 设置发布时间
			this.quesInfoService.saveQuesInfo(qim);
		}
		return "redirect:/question/quesManage/opt-query/quesInfoQuery.do";
	}

	/**
	 * 取消文件编辑操作-回滚新增的问题
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-modify/rollbackItem" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String rollbackItem(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String paperId) {
		String returnValue = "";
		QuestionInfoModel qim = this.quesInfoService.getQuesInfoById(paperId);
		try {
			if (!DataUtil.isNotNull(qim)) {// 只有新增文件取消时才可以回滚问题
				this.quesInfoService.deletePaperItem(paperId);
				this.quesInfoService.deleteQnrmInfo(paperId, null);
			}
			returnValue = "success";
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}

	/**
	 * 删除当前问卷
	 * 
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-delete/quesInfoDelete" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String quesInfoDelete(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String paperId) {
		String returnValue = "";
		try {
			if (DataUtil.isNotNull(paperId)) {
				QuestionInfoModel qim = this.quesInfoService
						.getQuesInfoById(paperId);
				String paperStatus = qim.getStatusDic().getCode();
				if (Constants.PAPER_STATUS.STATUS_SAVE.toString().equals(
						paperStatus)) {// 保存状态下【物理删除】
					this.quesInfoService.deletePaperItem(paperId);
					this.quesInfoService.deleteQnrmInfo(paperId, null);
				} else if (Constants.PAPER_STATUS.STATUS_DISABLE.toString()
						.equals(paperStatus)) {// 禁用状态下【逻辑删除】
					this.quesInfoService.abandonPaperItem(paperId);
					// 逻辑删除问卷-人员列表【??? 暂不需要】
				}
			}
			returnValue = "success";
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}

	/**
	 * 新增试卷问题
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/question/questionInfo/opt-save/quesInfoSave" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String quesInfoSave(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String paperId, String itIds,
			String itNames, String itCategoryTypes, String itTypes) {

		String itIdArray[] = DataUtil.isNotNull(itIds) ? itIds.split(",")
				: new String[0];
		String itNameArray[] = DataUtil.isNotNull(itNames) ? itNames.split(",")
				: new String[0];
		String itCategoryArray[] = DataUtil.isNotNull(itCategoryTypes) ? itCategoryTypes
				.split(",") : new String[0];
		String itTypeArray[] = DataUtil.isNotNull(itTypes) ? itTypes.split(",")
				: new String[0];
		if (DataUtil.isNotNull(paperId)) {
			this.quesInfoService.deletePaperItemOnly(paperId);
			for (int i = 0; i < itIdArray.length; i++) {
				QuestionInfoItemModel existPo = this.quesInfoService
						.querySinglePaperItem(paperId, itIdArray[i]);
				if (!DataUtil.isNotNull(existPo)) {// 针对不存在的试题，进行新增
					QuestionInfoItemModel qiim = this.formatePager(paperId,
							itIdArray[i], itNameArray[i], itCategoryArray[i],
							itTypeArray[i]);
					this.quesInfoService.savePaperItem(qiim);
				}
			}
		}
		return "success";
	}

	/**
	 * 查看试卷问题
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC + "/nsm/viewItem" })
	public String viewItem(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String pk) {
		QuestionInfoItemModel qiim = this.quesInfoService
				.getQuesInfoItemByPK(pk);
		if (DataUtil.isNotNull(qiim)
				&& DataUtil.isNotNull(qiim.getQuestionItem())) {
			List<QuestionItemOptionModel> optionList = this.quesInfoService
					.getItemOptioinList(qiim);
			model.addAttribute("itemTypeName", qiim.getItemType().getName());
			model.addAttribute("itemName", qiim.getItemName());
			Dic itemType = QuestionNaireConstants.ITEM_TYPE_MULESSAY_QUESTION;
			if (qiim.getItemType().getCode().equals(itemType.getCode())) {// 问答题无答案
				model.addAttribute("quesOptionList", null);
			} else {
				model.addAttribute("quesOptionList",
						(optionList.size() > 0) ? optionList : null);
			}
		}
		return Constants.QUESTIONNAIRE_STATISTIC + "/paperItemView";
	}

	/**
	 * 启用问卷
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-modify/openPapaer" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String openPapaer(HttpServletRequest request,
			HttpServletResponse response, String id) {
		QuestionInfoModel quesInfoModel = this.quesInfoService
				.getQuesInfoById(id);
		quesInfoModel.setStatusDic(Constants.PAPER_STATUS_ENABLE);
		quesInfoModel.setReleaseDate(DateUtil.getCurDate());
		this.quesInfoService.updateQuesInfo(quesInfoModel);
		return "success";
	}

	/**
	 * 禁用问卷
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-modify/shutdownPaper" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String shutdownPaper(HttpServletRequest request,
			HttpServletResponse response, String id) {
		QuestionInfoModel quesInfoModel = this.quesInfoService
				.getQuesInfoById(id);
		quesInfoModel.setStatusDic(Constants.PAPER_STATUS_DISABLE);
		quesInfoModel.setReleaseDate(DateUtil.getCurDate());
		this.quesInfoService.updateQuesInfo(quesInfoModel);
		return "success";
	}

	/**
	 * 删除试卷问题
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "question/quesManage/opt-modify/deleteItem" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String deleteItem(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String pk) {
		String returnValue = "";
		QuestionInfoItemModel qiim = this.quesInfoService
				.getQuesInfoItemByPK(pk);
		String rankColumn = "quesSeqNum";// 排序字段属性
		int rankValue = qiim.getQuesSeqNum();// 当前对象序号
		String fkColumn = "questionInfo";// 外键对象属性
		String fkValue = qiim.getQuestionInfo().getId();// 外键值
		try {
			if (DataUtil.isNotNull(qiim)
					&& DataUtil.isNotNull(qiim.getQuestionItem())) {
				// 先删除选项
				this.quesInfoService.deleteItemOption(qiim.getQuestionItem()
						.getId());
				// 再删除题目
				this.rankService.deleteCurObject(qiim, rankColumn, rankValue,
						fkColumn, fkValue);
			}
			returnValue = "success";
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}

	/**
	 * 封装问卷-试题对象
	 * 
	 * @param paperId
	 *            问卷ID
	 * @param itemId
	 *            问题ID
	 * @param itemName
	 *            问题名称
	 * @param catetoryId
	 *            问题分类ID
	 * @param itemTypeId
	 *            问题类型ID
	 * @return
	 */
	private QuestionInfoItemModel formatePager(String paperId, String itemId,
			String itemName, String catetoryId, String itemTypeId) {
		QuestionInfoItemModel qiimPo = new QuestionInfoItemModel();
		// 封装问卷对象
		QuestionInfoModel qifm = new QuestionInfoModel();
		qifm.setId(paperId);
		qiimPo.setQuestionInfo(qifm);

		// 封装问题对象
		QuestionItemModel qim = this.quesItemService.queryItemById(itemId);
		qiimPo.setQuestionItem(qim);

		// 封装问题是否必答
		qiimPo.setItemRequired(qim.getRequired());

		// 设置当前问题序号
		List<QuestionInfoItemModel> qiimList = this.quesInfoService
				.getQuesItemByInfoId(paperId);
		qiimPo.setQuesSeqNum(qiimList.size() + 1);

		// 设置问题类型
		Dic itemCategory = new Dic();
		itemCategory.setId(catetoryId);
		qiimPo.setItemCategory(itemCategory);

		// 设置问题名称
		qiimPo.setItemName(itemName);

		Dic itemType = dicService.getDic(itemTypeId);

		if (DataUtil.isNotNull(itemType)) {
			// 设置题型
			qiimPo.setItemType(itemType);
			// 设置题型序号
			qiimPo.setItemTypeSeq(itemType.getSeqNum());
		}

		// 设置逻辑删除状态
		qiimPo.setDelStatusDic(Constants.STATUS_NORMAL);

		return qiimPo;
	}

	/**
	 * 跳转到问卷查看页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-query/quesInfoView" })
	public String quesInfoView(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String id) {
		QuestionInfoModel questionInfo = null;
		if (DataUtil.isNotNull(id)) {
			questionInfo = this.quesInfoService.getQuesInfoById(id);
		} else {
			questionInfo = new QuestionInfoModel();
		}
		List<Dic> typeList = Constants.paperTypeList;
		List<QuestionInfoItemModel> questionItemList = quesInfoService
				.getQuesItemByInfoId(id);
		// model.addAttribute("typeList", typeList);
		// model.addAttribute("questionItemList", questionItemList);
		// model.addAttribute("questionInfo", questionInfo);
		model.addAttribute("questionItemList", questionItemList);
		model.addAttribute("questionInfo", questionInfo);
		model.addAttribute("itemTypeList", Constants.itemTypeList);
		model.addAttribute("categoryList", Constants.paperTypeList);
		model.addAttribute("paperTypeList", Constants.paperTypeList);
		model.addAttribute("commonTypeId",
				Constants.QUESINFO_TYPE_COMMON.getId());
		model.addAttribute("page", this.getPaperStudent(id));
		model.addAttribute("teacherPage", this.getPaperTeacher(id));
		return Constants.QUESTIONNAIRE_STATISTIC + "/quesInfoView";
	}

	/**
	 * 上移问题
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ClassNotFoundException
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-modify/moveUpItem" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String moveUpItem(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String returnValue = "";
		QuestionInfoItemModel qiim = this.quesInfoService
				.getQuesInfoItemByPK(id);
		try {
			if (DataUtil.isNotNull(qiim)) {
				String rankColumn = "quesSeqNum";// 排序字段属性
				int rankValue = qiim.getQuesSeqNum();// 当前对象序号
				String fkColumn = "questionInfo";// 外键对象属性
				String fkValue = qiim.getQuestionInfo().getId();// 外键值
				boolean isMinSeq = this.rankService.isMinSeq(qiim, rankColumn,
						rankValue, fkColumn, fkValue);
				if (isMinSeq) {
					returnValue = "min";
				} else {
					this.rankService.moveUpObject(qiim, rankColumn, rankValue,
							fkColumn, fkValue);
					returnValue = "success";
				}
			} else {
				returnValue = "null";
			}
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}

	/**
	 * 下移问题
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-modify/moveDownItem" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String moveDownItem(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String returnValue = "";
		QuestionInfoItemModel qiim = this.quesInfoService
				.getQuesInfoItemByPK(id);
		try {
			if (DataUtil.isNotNull(qiim)) {
				String rankColumn = "quesSeqNum";// 排序字段属性
				int rankValue = qiim.getQuesSeqNum();// 当前对象序号
				String fkColumn = "questionInfo";// 外键对象属性
				String fkValue = qiim.getQuestionInfo().getId();// 外键值
				boolean isMaxSeq = this.rankService.isMaxSeq(qiim, rankColumn,
						rankValue, fkColumn, fkValue);
				if (isMaxSeq) {
					returnValue = "max";
				} else {
					this.rankService.moveDownObject(qiim, rankColumn,
							rankValue, fkColumn, fkValue);
					returnValue = "success";
				}
			} else {
				returnValue = "null";
			}
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}

	/**
	 * 通过问卷名称进行唯一验证
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-query/quesInfoCheck" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String checkQuesInfoExist(HttpServletRequest request,
			HttpServletResponse response) {
		// 问卷id
		String quesInfoId = request.getParameter("id");
		// 问卷名称
		String name = request.getParameter("name");
		List<QuestionInfoModel> quesInfoList = this.quesInfoService
				.getQuesInfoByInfoName(name);
		if (quesInfoList != null && quesInfoList.size() > 0) {
			if (quesInfoList.size() == 1) {
				QuestionInfoModel quesInfoCheck = quesInfoList.get(0);
				if (!quesInfoCheck.getId().equals(quesInfoId)) {
					// 信息已存在
					return "false";
				}
			} else if (quesInfoList.size() > 1) {
				// 信息已经存在
				return "false";
			}
		}
		return "true";
	}

	/**
	 * 异步加载问题列表
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC
			+ "/nsm/asynLoadQuestion" })
	public String asynLoadQuestion(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, QuestionInfoModel questionInfo) {
		List<QuestionInfoItemModel> questionItemList = new ArrayList<QuestionInfoItemModel>();
		if (DataUtil.isNotNull(questionInfo)
				&& DataUtil.isNotNull(questionInfo.getId())) {
			questionItemList = quesInfoService.getQuesItemByInfoId(questionInfo
					.getId());
			if (DataUtil.isNotNull(questionInfo.getPaperCategory())) {
				Dic typeDic = new Dic();
				typeDic.setId(questionInfo.getPaperCategory());
				questionInfo.setTypeDic(typeDic);
			}
		} else {
			questionInfo = new QuestionInfoModel();
		}
		model.addAttribute("questionInfo", questionInfo);
		model.addAttribute("questionItemList", questionItemList);
		return Constants.QUESTIONNAIRE_STATISTIC + "/paperItemList";
	}

	/**
	 * 初始化教师的授权列表
	 * 
	 * @param request
	 *            当次请求
	 * @param response
	 *            当次响应
	 * @param model
	 *            页面模型
	 */
	@RequestMapping(value = { "/question/quesWrite/opt-edit/initTeacherInfo" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String initTeacherInfo(HttpServletRequest request,
			HttpServletResponse response, ModelMap model, String paperId,
			String teacherIds, String teacherNames) {
		String teacherIdArray[] = teacherIds.split(",");
		String teacherNameArray[] = teacherNames.split(";");
		String userType = QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.TEACHER
				.toString();
		try {
			this.quesInfoService.deleteQnrmInfo(paperId, userType);
			for (int i = 0; i < teacherIdArray.length; i++) {
				String teacherId = teacherIdArray[i];
				String teacherName = teacherNameArray[i];
				QuestionNaireRespondentModel qnrm = this.formatQnrmInfo(
						paperId, teacherId, teacherName, userType);
				this.quesInfoService.saveBm(qnrm);
			}
			return "success";
		} catch (Exception e) {
			return "error";
		}
	}

	/**
	 * 异步加载教师列表1
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-query/teacherqueryOnPage" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String teacherqueryOnPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			String studentIds) {
		int pageNo = request.getParameter("pageNo") != null ? Integer
				.parseInt(request.getParameter("pageNo")) : 1;
		model.addAttribute("pageNo", pageNo);
		return "{\"success\":\"success\",\"userType\":\""
				+ QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.TEACHER
				+ "\",\"pageNo\":\"" + pageNo + "\"}";
	}

	/**
	 * 异步加载教师列表2
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC
			+ "/nsm/asynLoadTeacherList" })
	public String asynLoadTeacherList(ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			String teacherIds) {
		List<BaseTeacherModel> teacherList = new ArrayList<BaseTeacherModel>();
		int pageNo = request.getParameter("pageNo") != null ? Integer
				.parseInt(request.getParameter("pageNo")) : 1;
		String teacherIdsConditon = this.getCondition(teacherIds);
		Page page = this.quesInfoService.queryTeacherQuesInfo(
				teacherIdsConditon, pageNo, Constants.DEFALT_PAGE_SIZE);
		List<User> respondentList = (List<User>) page.getResult();
		for (User user : respondentList) {
			BaseTeacherModel teacherInfo = baseDateService.findTeacherById(user
					.getId());
			teacherList.add(teacherInfo);
		}

		page.setResult(teacherList);
		model.addAttribute("teacherPage", page);
		model.addAttribute("hiddenTeacherIds", teacherIds);
		return Constants.QUESTIONNAIRE_STATISTIC + "/teacherSelectedList";
	}

	/**
	 * 初始化学生的授权列表
	 * 
	 * @param request
	 *            当次请求
	 * @param response
	 *            当次响应
	 * @param model
	 *            页面模型
	 */
	@RequestMapping(value = { "/question/quesWrite/opt-edit/initStudentInfo" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String initStudentInfo(HttpServletRequest request,
			HttpServletResponse response, ModelMap model, String paperId,
			String studentIds, String stuNames) {
		String stuIdArray[] = studentIds.split(",");
		String stuNameArray[] = stuNames.split(";");
		String userType = QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.STUDENT
				.toString();
		try {
			this.quesInfoService.deleteQnrmInfo(paperId, userType);
			for (int i = 0; i < stuIdArray.length; i++) {
				String stuId = stuIdArray[i];
				String stuName = stuNameArray[i];
				StudentInfoModel stuInfo = studentCommonServie
						.queryStudentById(stuId);
				if (DataUtil.isNotNull(stuInfo)) {
					int curYear = DateUtil.getCurYear();
					int stuEnterYear = (DataUtil.isNotNull(stuInfo
							.getEnterYearDic()) && DataUtil.isNotNull(stuInfo
							.getEnterYearDic().getCode())) ? Integer
							.parseInt(stuInfo.getEnterYearDic().getCode()) : 0;
					if (curYear == stuEnterYear)
						userType = QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.NEW_STUDENT
								.toString();
				}
				QuestionNaireRespondentModel qnrm = this.formatQnrmInfo(
						paperId, stuId, stuName, userType);
				this.quesInfoService.saveBm(qnrm);
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * 异步加载学生列表1
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { Constants.QUESTIONNAIRE_STATISTIC
			+ "/opt-query/stuqueryOnPage" }, produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String stuqueryOnPage(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String studentIds) {
		int pageNo = request.getParameter("pageNo") != null ? Integer
				.parseInt(request.getParameter("pageNo")) : 1;
		model.addAttribute("pageNo", pageNo);
		return "{\"success\":\"success\",\"userType\":\""
				+ QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.STUDENT
				+ "\",\"pageNo\":\"" + pageNo + "\"}";
	}

	/**
	 * 异步加载学生列表2
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ Constants.QUESTIONNAIRE_STATISTIC
			+ "/nsm/asynLoadStuList" })
	public String asynLoadStuList(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String studentIds) {
		List<StudentInfoModel> stuList = new ArrayList<StudentInfoModel>();
		int pageNo = request.getParameter("pageNo") != null ? Integer
				.parseInt(request.getParameter("pageNo")) : 1;
		String stuIdConditon = this.getCondition(studentIds);
		Page page = this.quesInfoService.queryStuQuesInfo(stuIdConditon,
				pageNo, Constants.DEFALT_PAGE_SIZE);
		List<User> respondentList = (List<User>) page.getResult();
		for (User user : respondentList) {
			StudentInfoModel stuInfo = studentCommonServie
					.queryStudentById(user.getId());
			stuList.add(stuInfo);
		}

		page.setResult(stuList);
		model.addAttribute("page", page);
		model.addAttribute("hiddenStuIds", studentIds);
		return Constants.QUESTIONNAIRE_STATISTIC + "/stuSelectedList";
	}

	/**
	 * 获取查询条件
	 * 
	 * @param studentIds
	 *            ID字符串
	 * @return in(......)查询条件
	 */
	private String getCondition(String studentIds) {
		StringBuffer sbff = new StringBuffer();
		if (DataUtil.isNotNull(studentIds)) {
			sbff.append(" (");
			String stuArray[] = studentIds.split(",");
			for (int i = 0; i < stuArray.length; i++) {
				String stuId = stuArray[i];
				if (stuArray.length - 1 == i) {
					sbff.append("'" + stuId + "'");
				} else {
					sbff.append("'" + stuId + "'").append(",");
				}
			}
			sbff.append(")");
		} else {
			logger.error("选人控件获取的 userids为空 !");
		}
		return sbff.toString();
	}

	/**
	 * 封装问卷-答题人对象
	 * 
	 * @param paperId
	 *            问卷ID
	 * @param userId
	 *            用户ID
	 * @param userName
	 *            用户名称
	 * @param userType
	 *            用户类型
	 * @return
	 */
	private QuestionNaireRespondentModel formatQnrmInfo(String paperId,
			String userId, String userName, String userType) {
		QuestionNaireRespondentModel qnrm = new QuestionNaireRespondentModel();
		// 封装问卷信息
		QuestionInfoModel questionNairePo = new QuestionInfoModel();
		questionNairePo.setId(paperId);
		qnrm.setQuestionNairePo(questionNairePo);

		// 封装用户信息
		User respondent = new User();
		respondent.setId(userId);
		qnrm.setRespondent(respondent);

		// 答题人姓名
		qnrm.setUserName(userName);

		// 封装答题人类型
		qnrm.setUserType(userType);

		// 封装记录状态
		qnrm.setDelStatusDic(Constants.STATUS_NORMAL);

		return qnrm;
	}

	/**
	 * 查询调查问卷信息
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param questionInfo
	 * @return
	 */
	@RequestMapping("/question/ques4S/opt-query/quesInfoQuery4S")
	public String queryQuesInfo4s(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, QuestionInfoModel questionInfo) {
		questionInfo.setStatusDic(dicUtil.getDicInfo("PAPER_STATUS",
				"STATUS_ENABLE"));
		int pageNo = request.getParameter("pageNo") != null ? Integer
				.parseInt(request.getParameter("pageNo")) : 1;
		Page page = this.quesInfoService.queryQuesInfo(questionInfo, pageNo,
				Page.DEFAULT_PAGE_SIZE);
		List<Dic> typeList = Constants.paperTypeList;// 问卷类型
		List<Dic> paperList = Constants.paperStatusList;// 问卷状态
		model.addAttribute("page", page);
		model.addAttribute("questionInfo", questionInfo);
		model.addAttribute("typeList", typeList);
		model.addAttribute("paperList", paperList);
		model.addAttribute("curpageNo", pageNo);

		return Constants.QUESTIONNAIRE_STATISTIC + "/quesInfoList4S";
	}

	/**
	 * 查询问卷已经答题的用户
	 */
	@RequestMapping("/question/quesUser/opt-query/queryUserByQuestion")
	public String queryUserByQuestion(ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			String queid, StudentInfoModel stu, QuestionInfoModel que) {
		if (DataUtil.isNull(queid)) {
			queid = que.getQuestionNaireId();
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer
				.parseInt(request.getParameter("pageNo")) : 1;
		Page page = this.quesStatisticService.queryStudentsByQuestion(10,
				pageNo, stu, queid);
		model.addAttribute("page", page);
		model.addAttribute("curpageNo", pageNo);
		model.addAttribute("queid", queid);
		model.addAttribute("stu", stu);
		return Constants.QUESTIONNAIRE_STATISTIC + "/quesUsers";
	}

	/**
	 * 导出问卷的答题情况
	 */
	@ResponseBody
	@RequestMapping("/question/quesUser/opt-query/exportAnswerInfo")
	public void exportAnswerInfo(ModelMap model, HttpServletRequest request,
			HttpServletResponse response, String queid) {

		QuestionInfoModel qim = this.questionNaireService
				.getQuestionNaireInfo(queid);
		HSSFWorkbook outWork = getHSSF(qim.getName());

		List<QuestionInfoItemModel> singleItemList = null;
		List<QuestionInfoItemModel> mulItemList = null;
		List<QuestionInfoItemModel> subItemList = null;

		// 获取问卷单选题列表
		singleItemList = this.questionNaireService
				.getQuestionNaireSingleItemList(queid);
		// 获取问卷多选题列表
		mulItemList = this.questionNaireService
				.getQuestionNaireMulItemList(queid);
		// 获取问卷问答题列表
		subItemList = this.questionNaireService
				.getQuestionNaireSubItemList(queid);

		List<QuestionInfoItemModel> questionAll = new ArrayList<QuestionInfoItemModel>();
		questionAll.addAll(singleItemList);
		questionAll.addAll(mulItemList);
		questionAll.addAll(subItemList);
		writeExcelTitle(outWork, singleItemList, mulItemList, subItemList); // 写入第二行数据

		List<QuestionItemOptionModel> singleQuesOptionList = null;
		List<QuestionItemOptionModel> mulQuesOptionList = null;
		List<QuestionItemOptionModel> subQuesAnswerList = null;
		// 获取了答题的学生信息
		List<Object> answerStudent = this.quesStatisticService
				.queryQuestionAnswerByQuestionId(queid);
		int i = 2;
		for (Object ot : answerStudent) {
			Object[] o = (Object[]) ot;
			// 获取单选题答题列表
			singleQuesOptionList = this.questionNaireService
					.getSingleQuestionOption(o[2].toString(), queid);
			// 获取多选题答题列表
			mulQuesOptionList = this.questionNaireService.getMulQuestionOption(
					o[2].toString(), queid);
			// 获取问答题答题列表
			subQuesAnswerList = this.questionNaireService
					.getAnswerQuestionOption(o[2].toString(), queid);

			HSSFRow row = outWork.getSheetAt(0).createRow(i); // 拿到第i+1行

			row.createCell(0).setCellValue(o[0] + "");
			row.createCell(1).setCellValue(o[2] + "");
			int index = 2; // 当前是第几个cell

			for (QuestionInfoItemModel qa : questionAll) { // 遍历所有的题

				if (qa.getItemType()
						.getId()
						.equals(QuestionNaireConstants.ITEM_TYPE_SINGLE.getId())) { // 单选题

					for (QuestionItemOptionModel q : singleQuesOptionList) { // 单选题

						if (qa.getId().equals(q.getPaperItemId())
								&& "CHECKED".equals(q.getChecked())) {// 这个是答案
							row.createCell(index)
									.setCellValue(
											q.getOptionCode() + "、"
													+ q.getOptionName());
							index++;
							break;
						}
					}
				} else if (qa
						.getItemType()
						.getId()
						.equals(QuestionNaireConstants.ITEM_TYPE_MULTIPLE
								.getId())) { // 多选题

					StringBuffer ans = new StringBuffer("");
					for (QuestionItemOptionModel q : mulQuesOptionList) { // 多选题
						if (qa.getId().equals(q.getPaperItemId())
								&& "CHECKED".equals(q.getChecked())) {// 这个是答案
							ans.append(q.getOptionCode() + "、"
									+ q.getOptionName() + " ");
						}
					}
					row.createCell(index).setCellValue(ans.toString());
					index++;

				} else { // 问答题
					for (QuestionItemOptionModel q : subQuesAnswerList) { // 问答题
						if (qa.getId().equals(q.getPaperItemId())) {// 这个是答案
							row.createCell(index).setCellValue(q.getAnswer());
							index++;
						}
					}
				}
			}

			i++;

		}
		export(outWork, response);
	}

	/**
	 * 写入数据的标题行
	 * 
	 * @Description: TODO
	 * @author: 唐靖
	 * @date: 2016-11-30 下午4:53:33
	 * @param outWork
	 * @param singleItemList
	 * @param mulItemList
	 * @param subItemList
	 */
	private void writeExcelTitle(HSSFWorkbook outWork,
			List<QuestionInfoItemModel> singleItemList,
			List<QuestionInfoItemModel> mulItemList,
			List<QuestionInfoItemModel> subItemList) {
		HSSFRow row = outWork.getSheetAt(0).createRow(1); // 拿到第二行

		row.createCell(0).setCellValue("姓名");
		row.createCell(1).setCellValue("学号");
		int i = 2;
		for (QuestionInfoItemModel q : singleItemList) { // 单选题
			row.createCell(i).setCellValue(q.getItemName());
			outWork.getSheetAt(0).setColumnWidth(i, 20 * 256);
			i++;
		}
		for (QuestionInfoItemModel q : mulItemList) { // 多选题
			row.createCell(i).setCellValue(q.getItemName());
			outWork.getSheetAt(0).setColumnWidth(i, 20 * 256);
			i++;
		}
		for (QuestionInfoItemModel q : subItemList) { // 问答题
			row.createCell(i).setCellValue(q.getItemName());
			outWork.getSheetAt(0).setColumnWidth(i, 20 * 256);
			i++;
		}
		// 完事

	}

	/**
	 * 查看学生答题情况
	 * 
	 * @Description: TODO
	 * @author: 唐靖
	 * @date: 2016-11-29 下午1:42:59
	 * @param request
	 * @param response
	 * @param model
	 * @param paperId
	 * @return
	 */
	@RequestMapping("/question/ques/opt-query/toQuestionAnswerShow.do")
	public String toQuestionAnswerShow(HttpServletRequest request,
			HttpServletResponse response, ModelMap model, String paperId,
			String userId) {
		QuestionInfoModel qim = this.questionNaireService
				.getQuestionNaireInfo(paperId);
		List<QuestionInfoItemModel> singleItemList = null;
		List<QuestionInfoItemModel> mulItemList = null;
		List<QuestionInfoItemModel> subItemList = null;
		List<QuestionItemOptionModel> singleQuesOptionList = null;
		List<QuestionItemOptionModel> mulQuesOptionList = null;
		List<QuestionItemOptionModel> subQuesAnswerList = null;
		if (DataUtil.isNotNull(qim) && DataUtil.isNotNull(qim.getId())) {
			// 获取问卷主键
			String questionNaireId = qim.getId();
			// 获取问卷单选题列表
			singleItemList = this.questionNaireService
					.getQuestionNaireSingleItemList(questionNaireId);
			// 获取问卷多选题列表
			mulItemList = this.questionNaireService
					.getQuestionNaireMulItemList(questionNaireId);
			// 获取问卷问答题列表
			subItemList = this.questionNaireService
					.getQuestionNaireSubItemList(questionNaireId);
			// 获取单选题答题列表
			singleQuesOptionList = this.questionNaireService
					.getSingleQuestionOption(userId, questionNaireId);
			// 获取多选题答题列表
			mulQuesOptionList = this.questionNaireService.getMulQuestionOption(
					userId, questionNaireId);
			// 获取问答题答题列表
			subQuesAnswerList = this.questionNaireService
					.getAnswerQuestionOption(userId, questionNaireId);
		}

		model.addAttribute("qim", qim);
		model.addAttribute("singleItemList", singleItemList);
		model.addAttribute("mulItemList", mulItemList);
		model.addAttribute("subItemList", subItemList);
		model.addAttribute("singleQuesOptionList", singleQuesOptionList);
		model.addAttribute("mulQuesOptionList", mulQuesOptionList);
		model.addAttribute("subQuesAnswerList", subQuesAnswerList);
		model.addAttribute("splitFlag",
				QuestionNaireConstants.AMS_SPLIT_FLAG_QUESTIONNAIRE);
		return "/common/question/userPaperView";
	}

	/**
	 * 创建导出文件
	 * 
	 * @Description: TODO
	 * @author: 唐靖
	 * @date: 2016-11-30 下午4:47:50
	 * @param name
	 * @return
	 */
	private HSSFWorkbook getHSSF(String name) {
		// 需要输出excel
		HSSFWorkbook outBook = new HSSFWorkbook();
		HSSFSheet firstSheet = outBook.createSheet(name + "答案文件"); // 设置工作区间的名字
		firstSheet.autoSizeColumn(1, true); // 列宽自适应
		CellRangeAddress cra = new CellRangeAddress(0, 0, 0, 2);
		firstSheet.addMergedRegion(cra);
		firstSheet.createRow(0); // 创造第一行
		firstSheet.getRow(0).createCell(0).setCellValue("问卷名称:" + name);
		return outBook;
	}

	private void export(HSSFWorkbook outBook, HttpServletResponse response) {
		if (outBook != null) {
			try {
				String fileName = outBook.getSheetAt(0).getSheetName() + ".xls";
				fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
				String headStr = "attachment; filename=\"" + fileName + "\"";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", headStr);
				OutputStream out = response.getOutputStream();
				outBook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
