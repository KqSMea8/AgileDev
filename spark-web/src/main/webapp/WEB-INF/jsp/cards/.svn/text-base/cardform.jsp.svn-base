<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	
<spark:template page="../templates/space.jsp" styles="js/autocomplete/jquery.autocomplete.css"
	scripts="js/tinymce/jscripts/tiny_mce/tiny_mce.js,js/tree/jquery.tree.min.js,js/spark-treeSelector.js,js/spark-menu.js,js/autocomplete/jquery.autocomplete.js,js/pages/cards/attachment.js,js/jqueryfileupload/ajaxfileupload.js,js/pages/cards/cardform.js, ${ _scripts }">

	<c:if test="${card.sequence == null}"><%--卡片id为空，创建新卡片 --%>
		<c:set var="method" value="post"/>
		<c:url var="actionPath" value="/spaces/${card.space.prefixCode}/cards"></c:url>
		<c:url var="backUrl" value="/spaces/${card.space.prefixCode}/cards/list"></c:url>
	</c:if>
	<c:if test="${card.sequence != null}">
		<c:set var="method" value="put"/>
		<c:url var="actionPath" value="/spaces/${card.space.prefixCode}/cards/${card.sequence }"></c:url>
		<c:url var="backUrl" value="/spaces/${card.space.prefixCode}/cards/${card.sequence }"></c:url>
	</c:if >
	<%--message div --%>
	<div id="card-contents">
		<spark:template page="../templates/info.jsp"/>
		<form:form id="cardform" action="${actionPath}" method="${method}" modelAttribute="card" onsubmit="return false;">
		<form:errors path="*" cssClass="error-message" element="div" ></form:errors>
		<form:hidden path="id"/>

		<%--由checkbox来控制是否提交parent，避免parent为空对象 --%>
		<input type="text" class="parent_id_of_this_card" id="parent.id" name="parent.id" value="${card.parent.id }" <c:if test="${card.parent.id!=null }">checked</c:if>  style="display:none"/>

		<form:hidden path="sequence"/>
		<form:hidden path="type.id"/>
		<form:hidden path="space.id"/>
		<form:hidden path="space.prefixCode"/>
		<input type="hidden" id="card-notify-emails" name="notifyEmails" value="" />
		<input type="hidden" id="card-notify-message" name="notifyMessage" value="" />
		<input type="hidden" id="card-attachment-ids" name="attachmentIds" value="" />
		
		<!-- nav-bar -->
		<c:set var="color" value="${ card.type.color == null ? 'F8F8F8' : card.type.color }"></c:set>
		<div class="card">
			<div class="card-top" style="border-left: 4px solid #${color};">
				<div class="card-action right" style="text-align: right;">
					<a class="card-save" href="javascript:void(0);"><fmt:message key="button.save" /></a>
					<%-- 编辑页面没有继续添加 --%>
					<c:if test="${card.sequence == null}">
					<%--继续添加 --%>
					<input type="hidden" id="addNext" name="addNext" value="0"/>
					<a class="card-add" href="javascript:void(0);"><fmt:message key="button.saveNext" /></a>
					</c:if>
					<c:if test="${card.sequence != null}">
						<a class="card-delete" href="javascript:void(0)" onclick="onDeleteCard('${card.space.prefixCode}',${card.sequence});"><fmt:message key="button.delete" /></a>
					</c:if>
					<a class="card-back" href="javascript:void(0);" ><fmt:message key="button.cancel" /></a>
				</div>
				<div class="card-edit-title">
					<form:input path="title"/>
					<c:if test="${card.sequence != null}">
					<div class="date-info">
						<span>${card.createdUser.name} </span>
						<fmt:message key="card.show.createdAt"/><span>
						<spark:date value="${card.createdTime}" /></span>&nbsp;
						<span>${card.lastModifiedUser.name} </span><fmt:message key="card.show.lastModifiedAt"/>
						<span><spark:date value="${card.lastModifiedTime}" /></span>
					</div>
					</c:if>
				</div>
			</div>
			<div class="card-middle">
				<div class="card-edit-detail" style="background: white;"><form:textarea path="detail" style="width:100%;"/></div>
				<div class="card-properties">
					<c:forEach items="${ cardPropertyValueList }" var="value" >
						<span class="property">
							<span class="label">${ value.cardProperty.name }:</span>
							<span class="value"><spark:template page="property/${ value.cardProperty.type }.jsp" value="${ value}" showType="edit" /></span>
						</span>
					</c:forEach>
					<%-- 只有当空间内存在icafe项目时才显示空间列表 --%>
					<c:if test="${ space.projects != null && fn:length(space.projects) > 0 }">
					<span class="property">
					 	<span class="label"><fmt:message key="card.project"/>: </span>
					 	<span class="value">
					 		<select name="project.id">
					 			<option value=""></option>
					 			<c:forEach items="${ space.projects }" var="project">
					 			<option value="${ project.id }" <c:if test="${ card.project.id == project.id }">selected="selected"</c:if> >${ project.name }</option>
					 			</c:forEach>
					 		</select>
						</span> 
					</span>
					</c:if>
					<span class="property">
					 	<span class="label"><fmt:message key="card.parent"/>: </span>
					 	<span class="value"><a id="parent_title_of_this_card" style="cursor:pointer;">
							<c:choose> 
								<c:when test="${card.parent.title != null}">
									 #${card.parent.sequence}-${card.parent.title}
								</c:when>
								<c:otherwise>
									<fmt:message key="card.parent.selectMessage"></fmt:message>
								</c:otherwise>
							</c:choose>
						</a></span> 
					</span>
				</div>
			</div>
			<div id= "newAttachmentContainer"> 
				<a class="add-button" id="add_uploader_button" name='add_uploader_button' href="javascript:void(0);">
					<fmt:message key="card.attachment.add"></fmt:message>
				</a>
			</div>
			
			<div class="card-bottom">
				<div class="card-action left">
					<a class="card-save" href="javascript:void(0);" ><fmt:message key="button.save" /></a>
					<%-- 编辑页面没有继续添加 --%>
					<c:if test="${card.sequence == null}">
					<a class="card-add" href="javascript:void(0);" ><fmt:message key="button.saveNext" /></a>
					</c:if>
					<c:if test="${card.sequence != null}">
						<a class="card-delete" href="javascript:void(0)" onclick="onDeleteCard('${card.space.prefixCode}',${card.sequence});"><fmt:message key="button.delete" /></a>
					</c:if>
					<a class="card-back" href="javascript:void(0);"><fmt:message key="button.cancel" /></a>
				</div>
				<div class="card-action-addons left">
					<label>
						<input type="checkbox" name="card-notify" id="card-notify" />
						<fmt:message key="notification.mail.action.check" />
					</label>
				</div>
			</div>
		</div>
		</form:form>
	</div>
	<div id="treeSelectorContainer" style="display:none;z-index:99;"></div>
	<spark:template page="../templates/card-delete-confirm.jsp"/>
</spark:template>

<script type="text/javascript">
(function(){
	var form = new Spark.pages.CardForm({
		isNew : ${card.sequence == null},
	 	spacePrefix : "${card.space.prefixCode}",
		sequence : "${card.sequence}",
		cardTypeId : "${card.type.id}",
		cardTypeName : "${card.type.name}",
		isRoot : "${card.parent == null}"
	 });
	 
	 $(".card-save").click(function(){
	 	$('#addNext').val('0');
	 	form.save();
	 });
	 
	 $(".card-add").click(function(){
	 	$('#addNext').val('1');
	 	form.save();
	 });
	 $(".card-back").click(function(){
	 	window.location.href =  "<%= request.getContextPath() %>/spaces/${card.space.prefixCode }/cards" + Spark.util.TabUtils.getLastView() ;
		return false;
	 });
})();
</script>
 