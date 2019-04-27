<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spark:template page="../templates/space.jsp">
	<form:form modelAttribute="space" method="post">
	<form:errors path="*"></form:errors>
    <table>
        <tr>
            <td><fmt:message key="space.name"/>:<font color="red">*</font></td>
            <td><form:input path="name" /></td>
        </tr>
        <tr>
            <td><fmt:message key="space.prefixCode"/>:<font color="red">*</font></td>
            <td><form:input path="prefixCode"/></td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="<fmt:message key="button.submit"/>"/>
            </td>
        </tr>
    </table>
</form:form>
</spark:template>