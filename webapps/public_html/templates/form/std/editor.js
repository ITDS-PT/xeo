//<SCRIPT>
function UpdateMessageBody(fieldh,valuefor)
{	
	if (fieldh.document.readyState == "complete")
	{
	fieldh.document.body.innerHTML=valuefor.innerHTML;
	fieldh.document.body.original=fieldh.document.body.innerHTML;
	}
}

function Save(event)
{
UpdateMessageBody();
}

