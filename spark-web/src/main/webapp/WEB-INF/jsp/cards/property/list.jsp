<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
	<c:choose>
		<c:when test="${_showType == 'show'}">
			<c:out value="${_value.displayValue}"/>
		</c:when>
		<c:when test="${_showType == 'edit'}">
			<select id="property_${_value.cardProperty.id}" name="property_${_value.cardProperty.id}">
					<option value=""></option>
				<c:forEach items="${_value.cardProperty.optionMap}" var="entry">
					<option value="${entry.key}"
						<c:if test="${_value.value==entry.key}">selected</c:if>
					>
						<c:out value="${entry.value}"/>
					</option>
				</c:forEach>
			</select>
		</c:when>
		<c:when test="${_showType == 'batch'}">
			<span id="properties_span_${_value.cardProperty.id}" class="query-right"></span>
			<script type="text/javascript">
			(function(){
				var valueSelectLink = $("<a>").attr("href", "javascript:void(0);").addClass("query-link").addClass("batch-query-link");
				var actualValue= $("<input>").attr("id", "property_${_value.cardProperty.id}").attr("type", "hidden").addClass("property-real-value").val(Spark.constants.RETAIN_FLAG);
				
				//生成下拉选项
				var callback = function (choice, key){
					valueSelectLink.text(choice.text());
					actualValue.val(key);
					if(actualValue.val() != Spark.constants.RETAIN_FLAG){
						retainButton.show();
					}
				};
				var options = new Array();
				options.push({ "txt" : Spark.util.message("listlink.select-empty"), "link" : callback, "props" : "" });
				<c:forEach items="${_value.cardProperty.optionMap}" var="entry">
					options.push({ "txt" : <spark:out value="${entry.value}" jsonValueStyle="true" default="\"\""/>, "link" : callback, "props" : ${entry.key} });
				</c:forEach>
				valueSelectLink.text(Spark.util.message("listlink.empty"));
				var retainButton =  $("<span>").addClass("retain-button").click(function(){
					actualValue.val(Spark.constants.RETAIN_FLAG);
					valueSelectLink.text(Spark.util.message("listlink.empty"));
					$(this).hide();
				}).hide();
				$("#properties_span_${_value.cardProperty.id}").append(valueSelectLink).append(retainButton).append(actualValue);
				new Spark.widgets.Menu( { "target" : valueSelectLink, "options" : options } );
			})();
			</script>
		</c:when>
		<c:otherwise>
			&nbsp;
		</c:otherwise>
	</c:choose>
