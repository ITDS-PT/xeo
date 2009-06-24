function bindMenu(treeName, menuName, menuItem, val)

{

		//<tree name='treeName' menuName=''><group>true</group></tree>

		//<tree name='treeName' menuName=''><parameters>true</parameters></tree>

		//<tree name='treeName' menuName=''><preview>right</preview></tree>

		//<tree name='treeName' menuName=''><preview>down</preview></tree>

		//<tree name='treeName' menuName=''><preview>off</preview></tree>

		var xml="<tree name='";

		xml+=treeName + "' menuName='"

		xml+=menuName+"'>";

		xml+="<"+menuItem+">"+val+"</"+menuItem+">";

		xml+="</tree>"

    boFormSubmit.boFormSubmitXml.value=xml;

		boFormSubmit.boFormSubmitMode.value = 15;

		boFormSubmit.submit();

}