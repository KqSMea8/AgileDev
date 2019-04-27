<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
	<c:choose>
		<c:when test="${_showType == 'show'}">
			${_value.displayValue}
		</c:when>
		<c:when test="${_showType == 'edit'}">
			<input id="property_${_value.cardProperty.id}" name="property_${_value.cardProperty.id}" type="text" value="${_value.displayValue}"/>
			<input id="datePicker_${_value.cardProperty.id}" name="datePicker_${_value.cardProperty.id}" type="hidden" onFocus=" WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd HH:mm',skin:'whyGreen', oncleared : function(){ $('#datePicker_${_value.cardProperty.id}').val(Spark.util.formatDate.today('format.date.long-datetime-minute')); $('#property_${_value.cardProperty.id}').text(Spark.util.message('listlink.select-empty'));}, })" value="${_value.displayValue}" />
			<script type="text/javascript">
			(function(){
				 $("#property_${_value.cardProperty.id}").focus(function(){ 
				 	$('#datePicker_${_value.cardProperty.id}').focus();
				 })
				 $("#property_${_value.cardProperty.id}").click(function(){ 
				 	$('#datePicker_${_value.cardProperty.id}').focus();
				 })
			})();
			</script>
		</c:when>
		<c:when test="${_showType == 'batch'}">
			<span id="properties_span_${_value.cardProperty.id}" class="query-right">
			<a id="display_datePicker_${_value.cardProperty.id}" class="query-link batch-query-link" href="javascript:void(0);" ><fmt:message key="listlink.editlink.empty"/></a>
			<input id="property_${_value.cardProperty.id}" type="hidden" class="property-real-value" value=""/ >
			<input id="datePicker_${_value.cardProperty.id}" type="hidden" onClick="WdatePicker({oncleared : function(){  $('#retainButton_${_value.cardProperty.id}').show(); $('#datePicker_${_value.cardProperty.id}').val(Spark.util.formatDate.today('format.date.long-datetime-minute')); $('#display_datePicker_${_value.cardProperty.id}').text(Spark.util.message('listlink.select-empty'));	$('#property_${_value.cardProperty.id}').val('');}, onpicked : function(){   if(!$('#datePicker_${_value.cardProperty.id}').val()){ $('#display_datePicker_${_value.cardProperty.id}').text(Spark.util.message('listlink.editlink.empty')); $('#property_${_value.cardProperty.id}').val(Spark.constants.RETAIN_FLAG); $('#retainButton_${_value.cardProperty.id}').hide();} else{	$('#display_datePicker_${_value.cardProperty.id}').text($(this).val());	$('#property_${_value.cardProperty.id}').val($(this).val());	$('#retainButton_${_value.cardProperty.id}').show();}}, isShowClear : true, dateFmt : 'yyyy-MM-dd HH:mm', skin : 'whyGreen', el : 'datePicker_${_value.cardProperty.id}' })"></input>
			<span id="retainButton_${_value.cardProperty.id}" class="retain-button" style="display:none"></span>
			</span>
			
			<script type="text/javascript">
			(function(){
				 $("#display_datePicker_${_value.cardProperty.id}").click(function(){
				 	$('#datePicker_${_value.cardProperty.id}').click();
				 })
				 
				 $("#retainButton_${_value.cardProperty.id}").click(function(){
				 	 $("#retainButton_${_value.cardProperty.id}").hide();
				 	 $('#display_datePicker_${_value.cardProperty.id}').text(Spark.util.message('listlink.editlink.empty'));	
					 $('#property_${_value.cardProperty.id}').val(Spark.constants.RETAIN_FLAG);
				 });
			})();
			</script>
		</c:when>
		<c:otherwise>
			&nbsp;
		</c:otherwise>
	</c:choose>
