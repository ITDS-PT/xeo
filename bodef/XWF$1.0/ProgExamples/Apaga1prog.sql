
insert into to_del (select BOUI from xwfreassign where boui in (select child$ from activity$reassigns where parent$ in (select boui from activity where program$ = 15719562)));
DELETE from xwfreassign where boui in (select child$ from activity$reassigns where parent$ in (select boui from activity where program$ = 15719562))                          ;

DELETE from activity$reassigns where parent$ in (select boui from activity where program$ = 15719562)                                                                         ;

insert into to_del (select BOUI from duration where boui in (select child$ from activity$workhistory where parent$ in (select boui from activity where program$ = 15719562))) ;
UPDATE duration set parent$ = null where boui in (select child$ from activity$workhistory where parent$ in (select boui from activity where program$ = 15719562)) ;

insert into to_del (select BOUI from duration where boui in (select child$ from xwfProgramRuntime$workhistory where parent$  = 15719562)) ;
UPDATE duration set parent$ = null where boui in (select child$ from xwfProgramRuntime$workhistory where parent$  = 15719562) ;

DELETE from activity$workhistory where parent$ in (select boui from activity where program$ = 15719562);
DELETE from xwfProgramRuntime$workhistory where parent$ = 15719562                                                                       ;
DELETE from xwfProgramRuntime$message where parent$ = 15719562 ;
DELETE from xwfProgramRuntime$access where parent$ = 15719562 ;
DELETE from xwfProgramRuntime$keys where parent$ = 15719562 ;

DELETE from activity$keys where parent$ in (select boui from activity where program$ = 15719562)                                                                              ;

DELETE from activity$keys_permissions where parent$ in (select boui from activity where program$ = 15719562)                                                                  ;

insert into to_del (select BOUI from xwfoption where boui in (select child$ from activity$options where parent$ in (select boui from activity where program$ = 15719562)))    ;
DELETE from xwfoption where boui in (select child$ from activity$options where parent$ in (select boui from activity where program$ = 15719562))                              ;

deLETE from activity$options where parent$ in (select boui from activity where program$ = 15719562)                                                                           ;

DELETE from activity$waitingresponse where parent$ in (select boui from activity where program$ = 15719562)                                                                   ;

DELETE from activity$inform where parent$ in (select boui from activity where program$ = 15719562)                                                                            ;

DELETE from activity$readlist where parent$ in (select boui from activity where program$ = 15719562)                                                                          ;

insert into to_del (select BOUI from xwfWait where program$ = 15719562)                                                                                                       ;
DELETE from xwfWait where program$ = 15719562                                                                                                                                 ;
insert into to_del (select value$ from xwfparticipant where boui in (select child$ from Xwfprogramruntime$participants where parent$ = 15719562))                                  ;

insert into to_del (select BOUI from xwfparticipant where boui in (select child$ from Xwfprogramruntime$participants where parent$ = 15719562))                               ;
DELETE from xwfparticipant where boui in (select child$ from Xwfprogramruntime$participants where parent$ = 15719562)                                                         ;
DELETE from Xwfprogramruntime$participants where parent$ = 15719562   ;

insert into to_del (select value$ from xwfvariable where boui in (select child$ from Xwfprogramruntime$variables where parent$ = 15719562))                                  ;
                                                                                                                                                
insert into to_del (select BOUI from xwfvariable where boui in (select child$ from Xwfprogramruntime$variables where parent$ = 15719562))                                     ;
DELETE from xwfvariable where boui in (select child$ from Xwfprogramruntime$variables where parent$ = 15719562)                                                               ;
DELETE from Xwfprogramruntime$variables where parent$ = 15719562                                                                                                              ;
    
insert into to_del (select value$ from xwfvariable where boui in (select child$ from activity$variables where parent$ in (select boui from activity where program$ = 15719562)));                                                                                                      
insert into to_del (select BOUI from xwfvariable where boui in (select child$ from activity$variables where parent$ in (select boui from activity where program$ = 15719562)));
DELETE from xwfvariable where boui in (select child$ from activity$variables where parent$ in (select boui from activity where program$ = 15719562) )                          ;                                                      
DELETE from activity$variables where parent$ in (select boui from activity where program$ = 15719562)                                                                         ;

insert into to_del (select value$ from xwfvariable where boui in (select message$ from activity where program$ = 15719562));                                                                                                         
insert into to_del (select BOUI from xwfvariable where boui in (select message$ from activity where program$ = 15719562));

       
insert into to_del (Select xwfProgramRuntime.boui from xwfProgramRuntime where boui = 15719562 )                                                                              ;
insert into to_del (Select boui from activity where program$ = 15719562 )                                                                                                     ;
delete from activity where program$ = 15719562  ;

insert into to_del (select BOUI from xwfannouncedetails where referenceobject$ in (select del_boui from to_del));
DELETE FROM xwfannouncedetails where referenceobject$ in (select del_boui from to_del);
DELETE FROM xwfannounce$details where CHILD$ in (select del_boui from to_del);
                                                                                                                             
DELETE from xwfvariable where boui in (select del_boui from to_del)  ;
DELETE FROM xwfVarValue where boui in (select del_boui from to_del)                                                          ;                                                                                                    
DELETE FROM ebo_references where exists (select del_boui from to_del where del_boui=boui) or exists (select del_boui from to_del where del_boui=refboui$);
DELETE FROM ebo_registry where ui$ in (select del_boui from to_del) and clsid <> 'duration'                                                                                                          ;
DELETE FROM ebo_textindex where ui$ in (select del_boui from to_del)                                                                                                         ;

DELETE from xwfProgramRuntime where boui = 15719562                                                                                                                           ;
                                                                                                                                                                             
truncate table to_del