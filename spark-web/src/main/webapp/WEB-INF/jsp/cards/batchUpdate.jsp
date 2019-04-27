<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	
<spark:template page="../templates/space.jsp" styles="js/autocomplete/jquery.autocomplete.css"
	scripts="js/spark-menu.js,js/autocomplete/jquery.autocomplete.js, ${ _scripts }">
	
	<script type="text/javascript">
		Spark.constants.RETAIN_FLAG = "<%= com.baidu.spark.model.card.property.CardPropertyValue.RETAIN_FLAG %>";
	</script>
	
	<div id="card-contents">
		<div id="error-message" class="info-message" style="display:none;">
			<span class="info-message-icon"></span>
		</div>
		<spark:template page="../templates/info.jsp"/>
		<input type="hidden" id="ids" name="ids" value="${ids}" />
		<!-- nav-bar -->
		<div class="card">
			<div class="card-top" >
				<div class="card-action right" style="text-align: right;">
					<a class="card-save" href="javascript:void(0);"><fmt:message key="button.save" /></a>
		 			<a class="card-back" href="javascript:void(0);" ><fmt:message key="button.cancel" /></a>
				</div>
				<div class="card-title title"><fmt:message key="card.batchUpdate" /><fmt:message key="card.batchUpdate.commonProperties" /></div>
			</div>
			<div class="card-middle">
				<div class="card-properties" style="padding-top:15px;">
					<c:forEach items="${ cardPropertyValueList }" var="value" >
						<span class="property">
							<span class="label">${ value.cardProperty.name }:</span>
							<span class="value"><spark:template page="property/${ value.cardProperty.type }.jsp" value="${ value}" showType="batch" /></span>
						</span>
					</c:forEach>
				</div>
			</div>
			
			<div id="slide-down" class="batch-selected-card">
				<div id="slide-down-icon"></div>
				<div id="slide-down-text"><fmt:message key="card.batchUpdate.selected.show" /></div>
			</div>
			
			<div id="product-cards" class="batch-product-cards" style="display:none;">
				<table id="list-table" class="list" >
					<thead>
						<tr id="cards-header" class="cards-header">
							<th><fmt:message key="card.sequence" /></th><th><fmt:message key="card.sequence" /></th><th><fmt:message key="card.cardType" /></th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="card" items="${cards}">
						<tr>
							<td>#${card.sequence}</td><td>${card.title}</td><td>${card.type.name}</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</div>
			
			<div id="slide-up" style="display:none;" class="batch-selected-card">
				<div id="slide-up-icon"></div>
				<div id="slide-up-text"><fmt:message key="card.batchUpdate.selected.hide" /></div>
			</div>
			
			<div class="card-bottom" >
				<div class="card-action left">
					<a class="card-save" href="javascript:void(0);" ><fmt:message key="button.save" /></a>
					<a class="card-back" href="javascript:void(0);"><fmt:message key="button.cancel" /></a>
				</div>
			</div> 
			
		</div>
	</div>
	
</spark:template>

<script type="text/javascript">
(function(){
	$(".card-save").click(function(){
		var data = {"ids" : "${ids}"};
		$("input.property-real-value").each(function(){
			data[$(this).attr("id")] = $(this).val();
		});
		
		$.ajax({
			url: "<%= request.getContextPath() %>/ajax/spaces/${space.prefixCode}/cards/batchUpdate",
			data : data,
			type : "POST" ,
			success : function(data){
				location.href = "<%= request.getContextPath() %>/spaces/${space.prefixCode}/cards/" + Spark.util.TabUtils.getLastView();
			},
			error : function(x, s, e){
				if(x.responseText && x.responseText.length < 300){
					$("#error-message").text(x.responseText).show();
				}else{
					$("#error-message").text(Spark.util.formatResponseStatus(x.status)).show();
				}
			}
		});
	})
	
	$(".card-back").click(function(){
		location.href = "<%= request.getContextPath() %>/spaces/${space.prefixCode}/cards/" + Spark.util.TabUtils.getLastView();
	})
	
	$("#slide-down").click(function(){ 
		$("#slide-down").hide();
		$("#product-cards").show();
		$("#slide-up").show();
	})

	$("#slide-up").click(function(){ 
		$("#slide-up").hide();
		$("#product-cards").hide();
		$("#slide-down").show();
	})
	
	
})();
</script>
 