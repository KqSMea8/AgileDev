<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
	<c:choose>
		<c:when test="${_showType == 'show'}">
			<c:out value="${_value.displayValue}"/>
		</c:when>
		<c:when test="${_showType == 'edit'}">
			<input type="text" id="property_${_value.cardProperty.id}" name="property_${_value.cardProperty.id}" value='<c:out value="${_value.displayValue}"/>'/>
		</c:when>
		<c:when test="${_showType == 'batch'}">
			<span id="properties_span_${_value.cardProperty.id}" class="query-right"></span>
			<script type="text/javascript">
			(function(){
				var callback = function(editBox){ 
					actualValue.val(editBox.val());
					if (!editBox.val()){
						editBox.val(Spark.util.message("listlink.select-empty"));
					}
					valueField.text(editBox.val());
					if(actualValue.val() != Spark.constants.RETAIN_FLAG){
						retainButton.show();
					}
				};
				var actualValue = $("<input>").attr("id", "property_${_value.cardProperty.id}")
					.attr("type", "hidden").addClass("property-real-value").val(Spark.constants.RETAIN_FLAG);
				var valueField = $("<a>").attr("href", "javascript:void(0);").click(function(){
					retainButton.hide();
				}).text(Spark.util.message("listlink.editlink.empty")).addClass("query-link").addClass("batch-query-link");
				var retainButton = $("<span>").addClass("retain-button").click(function(){
					actualValue.val(Spark.constants.RETAIN_FLAG);
					valueField.text(Spark.util.message("listlink.editlink.empty"));
					$(this).hide();
				}).hide();
				$("#properties_span_${_value.cardProperty.id}").append(valueField).append(retainButton).append(actualValue);

				new Spark.widgets.EditLink({ "target" : valueField, "link" : callback, "css" : "input-text-normal" });
			
			})()
			</script>
		</c:when>
		<c:otherwise>
			&nbsp;
		</c:otherwise>
	</c:choose>
