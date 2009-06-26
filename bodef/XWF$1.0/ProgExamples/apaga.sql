truncate table activity$reassigns;
truncate table activity$keys;
truncate table activity$parent$;
truncate table activity$keys_permissions;
truncate table activity$options;
truncate table activity$variables;
truncate table activity$waitingresponse;
truncate table activity$workhistory;
truncate table activity$inform;
truncate table activity$readlist;
update activity set program$=null;
delete activity where 1=1;

update activity set callprogram$=null;

truncate table Xwfprogramruntime$message;
truncate table Xwfprogramruntime$variables;
truncate table Xwfprogramruntime$participants;
truncate table xwfwait;
delete Xwfprogramruntime where 1=1;

truncate table xwfvarvalue$valuelist;
truncate table xwfvariable$availablemethods;
truncate table xwfvariable$hiddenmethods;
truncate table xwfparticipant;
truncate table xwfserialobject;
truncate table xwfreassign;

