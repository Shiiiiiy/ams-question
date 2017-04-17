package com.uws.question.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import com.uws.common.util.ChineseUtill;
import com.uws.common.util.QuestionNaireConstants;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionItemOptionModel;
import com.uws.domain.question.QuestionOptionModel;
import com.uws.domain.question.QuestionStatisticItemModel;
import com.uws.domain.question.QuestionStatisticModel;
import com.uws.domain.question.QuestionStatisticOptionModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.question.service.IQuesInfoService;
import com.uws.question.service.IQuesStatisticService;
import com.uws.question.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

@SuppressWarnings("deprecation")
@Controller
@RequestMapping(Constants.QUESTION_STATISTIC)
public class QuesStatisticController {

	//日志
	private Logger log = new LoggerFactory(QuesStatisticController.class);
	//数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IQuesInfoService quesInfoService;
	@Autowired
	private IQuesStatisticService quesStatisticService;
	
	/**
	 * 问卷统计—列表展示
	 * @param model
	 * @param request
	 * @param quesItem
	 * @return
	 */
	@RequestMapping({"/opt-query/quesStatisticQuery"})
	public String quesStatisticQuery(ModelMap model, HttpServletRequest request,QuestionInfoModel question){
		
		log.info("问卷统计查询列表");
		Integer pageNo = request.getParameter("pageNo")!=null?Integer.valueOf(request.getParameter("pageNo")):1;
		//查询已提交的所有问卷的Id
		Page page = quesStatisticService.queryQuesInfo(Page.DEFAULT_PAGE_SIZE, pageNo, question);
		@SuppressWarnings("unchecked")
		List<String> questionIdList =(List<String>) page.getResult();
		//存放临时对象的list
		List<QuestionStatisticModel> statisticList =new ArrayList<QuestionStatisticModel>();
		if(questionIdList!=null && questionIdList.size()>0){
			for(String quesId:questionIdList){
				QuestionInfoModel ques = quesInfoService.getQuesInfoById(quesId);
				QuestionStatisticModel quesStatisticInfo =new QuestionStatisticModel();
				//统计回答问卷的人数
				long answerNum = quesStatisticService.countQuesAnswer(quesId,ques.getTypeDic());
				quesStatisticInfo.setAnswerNum(answerNum);
				quesStatisticInfo.setQuestion(ques);
				statisticList.add(quesStatisticInfo);
			}
		}
		page.setResult(statisticList);
		model.addAttribute("page", page);
		model.addAttribute("question", question);
		model.addAttribute("categoryList",dicUtil.getDicInfoList("QUESINFO_TYPE"));
		return Constants.QUESTION_STATISTIC+"/quesStatisticList";
	}
	
	/**
	 * 问卷统计—统计分析页面
	 * @param model
	 * @param request
	 * @param questionId  问卷Id 
	 * @return
	 */
	@RequestMapping({"/opt-query/itemStatisticQuery"})
	public String itemStatisticQuery(ModelMap model, HttpServletRequest request ,String questionId){
		
		QuestionInfoModel question = quesInfoService.getQuesInfoById(questionId);
		//查询问卷下的题目列表
		List<QuestionStatisticItemModel> staItemList =new ArrayList<QuestionStatisticItemModel>();
		List<QuestionStatisticOptionModel> staOptionList =new ArrayList<QuestionStatisticOptionModel>();
		List<QuestionInfoItemModel> quesInfoItemList = quesInfoService.getItemByPaperId4Summery(questionId);
		
		if(quesInfoItemList!=null && quesInfoItemList.size()>0){
			for(QuestionInfoItemModel itemInfo:quesInfoItemList){
				//题目临时对象
				QuestionStatisticItemModel staItem = new QuestionStatisticItemModel();
				staItem.setQuestion(question);
				staItem.setItem(itemInfo.getQuestionItem());
				//查询某题目包含的所有选项信息
				staOptionList = queryStaOption(questionId,itemInfo.getQuestionItem().getId(),itemInfo.getItemType());
				staItem.setOptionList(staOptionList);
				staItem.setOptionListSize(staOptionList.size());
				staItemList.add(staItem);
			}
		}
		model.addAttribute("itemList", staItemList);
		model.addAttribute("questionId", questionId);
		model.addAttribute("quesName", question.getName());
		return Constants.QUESTION_STATISTIC+"/itemStatisticList";
	}

	/**
	 * 跳转到问卷查看页面
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({"/opt-query/quesInfoView"})
	public String quesInfoView(ModelMap model,HttpServletRequest request,HttpServletResponse response,String id){
		
		QuestionInfoModel questionInfo = null;
		if(DataUtil.isNotNull(id)){
			questionInfo = this.quesInfoService.getQuesInfoById(id);
		}else{
			questionInfo = new QuestionInfoModel();
		}
		List<Dic> typeList = Constants.paperTypeList;
		List<QuestionInfoItemModel> questionItemList = quesInfoService.getQuesItemByInfoId(id);
		model.addAttribute("typeList", typeList);
		model.addAttribute("questionItemList", questionItemList);
		model.addAttribute("questionInfo", questionInfo);
		return Constants.QUESTION_STATISTIC+"/questionInfoView";
	}	
	
	/**
	 * 查看试卷问题
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({"/nsm/viewItem"})
	public String viewItem(ModelMap model,HttpServletRequest request,HttpServletResponse response,String pk){
		QuestionInfoItemModel  qiim = this.quesInfoService.getQuesInfoItemByPK(pk);
		if(DataUtil.isNotNull(qiim) && DataUtil.isNotNull(qiim.getQuestionItem())){
			List<QuestionItemOptionModel> optionList = this.quesInfoService.getItemOptioinList(qiim);
			model.addAttribute("itemTypeName", qiim.getItemType().getName());
			model.addAttribute("itemName", qiim.getItemName());
			Dic  itemType = QuestionNaireConstants.ITEM_TYPE_MULESSAY_QUESTION;
			if(qiim.getItemType().getCode().equals(itemType.getCode())){//问答题无答案
				model.addAttribute("quesOptionList", null);
			}else{
				model.addAttribute("quesOptionList", (optionList.size()>0)?optionList:null);
			}
		}
		return Constants.QUESTION_STATISTIC+"/quesItemView";
	}
	
	/**
	 * 进入导出页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/nsm/exportQuesStatistic")
	public String exportCourseInfoList(ModelMap model,HttpServletRequest request){
		int exportSize=Integer.valueOf(request.getParameter("exportSize")).intValue();
		int pageTotalCount=1;
		int maxNumber=0;
		if(pageTotalCount<exportSize){
			maxNumber=1;
		}else if(pageTotalCount % exportSize == 0){
			maxNumber=pageTotalCount / exportSize;
		}else{
			maxNumber=pageTotalCount / exportSize + 1;
		}
		
		model.addAttribute("exportSize",Integer.valueOf(exportSize));
		model.addAttribute("maxNumber",Integer.valueOf(maxNumber));
		//为了能将导出的数据效率高，判断每次导出数据500条
		if(maxNumber<500){
			model.addAttribute("isMore", "false");
		}else{
			model.addAttribute("isMore", "true");
		}
		model.addAttribute("exportType","exportCountByCanal");//导出的类型
		model.addAttribute("exportName","调查问卷统计分析");
		return Constants.QUESTION_STATISTIC+"/exportQuesStatistic";
	}
	
	/**
	 * 导出调查问卷统计信息
	 * @param model
	 * @param request
	 * @param response void
	 */
	@RequestMapping({ "/opt-query/exportQuesStatistic" })
	public void exportTrainClassByCanal(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		
		String questionId =request.getParameter("questionId");
		String exportPage = request.getParameter("lecturerQuery_exportPage");
		QuestionInfoModel question = quesInfoService.getQuesInfoById(questionId);
		//查询问卷下的题目列表
		List<QuestionStatisticItemModel> staItemList =new ArrayList<QuestionStatisticItemModel>();
		List<QuestionStatisticOptionModel> staOptionList =new ArrayList<QuestionStatisticOptionModel>();
		List<QuestionInfoItemModel> quesInfoItemList = quesInfoService.getItemByPaperId4Summery(questionId);
		
		if(quesInfoItemList!=null && quesInfoItemList.size()>0){
			for(QuestionInfoItemModel itemInfo:quesInfoItemList){
				//题目临时对象
				QuestionStatisticItemModel staItem = new QuestionStatisticItemModel();
				staItem.setQuestion(question);
				staItem.setItem(itemInfo.getQuestionItem());
				//查询某题目包含的所有选项信息
				staOptionList = queryStaOption(questionId,itemInfo.getQuestionItem().getId(),itemInfo.getItemType());
				staItem.setOptionList(staOptionList);
				staItem.setOptionListSize(staOptionList.size());
				staItemList.add(staItem);
			}
		}
		HSSFWorkbook wb = null;
		if(staItemList!=null && staItemList.size()>0){
			//将最终数据，按照要求以Excel进行展示（要求题目名称和题型合并单元格）
			 wb = packHssf(staItemList);
		}
		String filename = "调查问卷统计分析第" + exportPage + "页.xls";
	    try {
	    	response.setContentType("application/x-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes("GBK"), "iso-8859-1"));
			response.setCharacterEncoding("UTF-8");
			ServletOutputStream fos = response.getOutputStream();
			wb.write(fos);
			fos.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}
	
	/**
	 * 导出的Excel结构及数据
	 * @param staOptionList
	 * @return
	 */
	public HSSFWorkbook packHssf(List<QuestionStatisticItemModel> staItemList) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
		//定义sheet页名称
		HSSFSheet sheet = wb.createSheet("问卷统计");
		
		//设定样式：居中显示
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		//第一行 标题行
		HSSFRow row1 = sheet.createRow(0);  
		HSSFCell cell0=row1.createCell(0);
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,5));
		cell0.setCellValue(staItemList.get(0).getQuestion().getName()+"_调查问卷统计分析");
		cell0.setCellStyle(style);
		
		//第二行 字段行
		HSSFRow row = sheet.createRow(1); 
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("序号");
		cell.setCellStyle(style);
		
		cell = row.createCell(1);
		cell.setCellValue("题目名称");
		cell.setCellStyle(style);
		
		cell = row.createCell(2);
		cell.setCellValue("题型");
		cell.setCellStyle(style);
		
		cell = row.createCell(3);
		cell.setCellValue("答案");
		cell.setCellStyle(style);
		
		cell = row.createCell(4);
		cell.setCellValue("选择人数");
		cell.setCellStyle(style);
		
		cell = row.createCell(5);
		cell.setCellValue("占比(%)");
		cell.setCellStyle(style);
		
		//标记每个题目中选项的个数
		int optionNum=0;
		int sumNum=0;
		QuestionStatisticItemModel staItem=new QuestionStatisticItemModel();
		
		//遍历显示所有题目
		for(int i=0;i<staItemList.size();i++){
			staItem =staItemList.get(i);
			optionNum = staItem.getOptionListSize();
			sumNum+=optionNum;
			//遍历显示所有选项
			List<QuestionStatisticOptionModel> optionList = staItem.getOptionList();
			if(optionList!=null && optionList.size()>0){
				//遍历题目中的所有选项,并将选项值set到对应的单元格中
				for(int j=0;j<optionList.size();j++){
					QuestionStatisticOptionModel option = optionList.get(j);
					HSSFRow row_j = sheet.createRow(sumNum-optionNum+2+j);
					//每次循环选项时，都先创建每行的合并单元格，并将值set到对应的单元格中
					if(j==0){
						//序号_合并单元格
						cell =row_j.createCell(0);
						sheet.addMergedRegion(new CellRangeAddress(sumNum-optionNum+2,sumNum+1,0,0));
						cell.setCellValue(i+1);
						cell.setCellStyle(style);
						
						//题目名称_合并单元格
						cell =row_j.createCell(1);
						sheet.addMergedRegion(new CellRangeAddress(sumNum-optionNum+2,sumNum+1,1,1));
						cell.setCellValue(staItem.getItem().getItemName());
						cell.setCellStyle(style);
						
						//题目类型_合并单元格
						cell = row_j.createCell(2);
						sheet.addMergedRegion(new CellRangeAddress(sumNum-optionNum+2,sumNum+1,2,2));
						cell.setCellValue(staItem.getItem().getItemType().getName());
						cell.setCellStyle(style);
					}
					//选项名称
					cell=row_j.createCell(3);
					cell.setCellValue(option.getOption().getOptionName());
					cell.setCellStyle(style);
					//选择人数
					cell=row_j.createCell(4);
					cell.setCellValue(option.getNum());
					cell.setCellStyle(style);
					//占比
					cell=row_j.createCell(5);
					cell.setCellValue(option.getPercent());
					cell.setCellStyle(style);
				}
			}
		}
		return wb;
	}	
	
	  /**
	   * 拼接柱状图要展示的数据
	   * @param model
	   * @param request
	   * @return
	   */
	@RequestMapping(value={"/comp/getItemJson"}, produces={"text/plain;charset=UTF-8"})
    public String getItemJson(ModelMap model,HttpServletRequest request) 
    {
		String questionId = request.getParameter("questionId");
    	String itemId = request.getParameter("itemId");
        String itemName=request.getParameter("itemName");
        //判断题目名称是否存在中文乱码
        boolean b = ChineseUtill.isMessyCode(itemName);
        if(b){
        	itemName = ChineseUtill.toChinese(itemName);
        }
        //获取某题目下的所有选项
    	List<QuestionStatisticOptionModel> staOptionList = queryStaOption(questionId,itemId,null);
    	if (staOptionList==null || staOptionList.size()<=0) {
	      return "[]";
	   }
    	//分别拼接x轴和Y轴的值
	    StringBuffer sbx = new StringBuffer("[");
	    StringBuffer sby = new StringBuffer("[{");
	    sby.append("name:").append("'").append("答案选项").append("',").append("data:[");
	    
	    int i = 0;
	    for (QuestionStatisticOptionModel optionSta:staOptionList)
	    {
	    	if(optionSta!=null){
	    		sbx.append("'").append(optionSta.getOption().getOptionName()).append("'");
	    		sby.append(optionSta.getNum());
	    	}
	    	if (i < staOptionList.size()-1) {
		        sbx.append(",");
		        sby.append(",");
		        i++;
		      }
	    }
	    model.addAttribute("iname",itemName);
	    model.addAttribute("xtext",sbx.append("]").toString());
	    model.addAttribute("ytext",sby.append("]}]").toString());
	    return Constants.QUESTION_STATISTIC+"/barChart" ;
    	
    }
	
	 /**
	   * 拼接饼状图要展示的数据
	   * @param model
	   * @param request
	   * @return
	   */
	@RequestMapping(value={"/comp/getPieJson"}, produces={"text/plain;charset=UTF-8"})
  public String getPieJson(ModelMap model,HttpServletRequest request) 
  {
	  String questionId = request.getParameter("questionId");
	  String itemId = request.getParameter("itemId");
	  String itemName=request.getParameter("itemName");
      //判断题目名称是否存在中文乱码
      boolean b = ChineseUtill.isMessyCode(itemName);
      if(b){
      	itemName = ChineseUtill.toChinese(itemName);
      }
      //获取某题目下的所有选项
  		List<QuestionStatisticOptionModel> staOptionList = queryStaOption(questionId,itemId,null);
  		if (staOptionList==null || staOptionList.size()<=0) {
	      return "[]";
  		}
  		//拼接饼状图需要展示的数据
	    StringBuffer sbx = new StringBuffer("[");
	    int i = 0;
	    for (QuestionStatisticOptionModel optionSta:staOptionList)
	    {
	    	if(optionSta!=null){
	    		sbx.append("['").append(optionSta.getOption().getOptionName()).append("',");
	    		sbx.append(optionSta.getNum());
	    		sbx.append("]");
	    	}
	    	if (i < staOptionList.size()-1) {
		        sbx.append(",");
		        i++;
		      }
	    }
	    model.addAttribute("iname",itemName);
	    model.addAttribute("pieData",sbx.append("]").toString());
	    return Constants.QUESTION_STATISTIC+"/pieChart" ;
  	
  }
	
	/**
	 * 统计题目所包含的每个选项的答题人数和比率
	 * @param questionId
	 * @param itemId
	 * @param itemType
	 * @return
	 */
	private List<QuestionStatisticOptionModel> queryStaOption(String questionId, String itemId,Dic itemType) {
		List<QuestionStatisticOptionModel> staOptionList = new ArrayList<QuestionStatisticOptionModel>();
		//用于统计每个题目的答题人数
		long sumNum =0;
		//根据问卷Id和题目Id查询对应的选项List
		List<QuestionOptionModel> optionList = quesStatisticService.queryOptionList(questionId,itemId);
		if(optionList!=null && optionList.size()>0){
			DecimalFormat df = new DecimalFormat("#.0");
			for(QuestionOptionModel option: optionList){
				QuestionStatisticOptionModel staOption = new QuestionStatisticOptionModel();
				//统计每个选项对应的答题人数
				long optionNum = quesStatisticService.countItemAnswer(questionId, itemId, option.getId(), itemType);
				sumNum+=optionNum;
				staOption.setNum(optionNum);
				staOption.setOption(option);
				staOptionList.add(staOption);
			}
			//统计每个选项答题人数的百分比
			if(staOptionList!=null && staOptionList.size()>0){
				for(QuestionStatisticOptionModel staOption:staOptionList){
					if(staOption.getNum()==0){
						staOption.setPercent("0");
					}else{
						double val=(0.0+staOption.getNum())/sumNum*100;
						staOption.setPercent(df.format(val));
					}
				}
			}
		}
		return staOptionList;
	}
	
}
