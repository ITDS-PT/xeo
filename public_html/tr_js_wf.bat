del x.js
del x1.js
del x2.js

copy bo_global.js b.js

trimjs b.js x.js
del b.js
copy bo_skin.js b.js
trimjs b.js x1.js
del b.js
copy ieLibrary\wkfl\xwfcontrol.js b.js
trimjs b.js x2.js
copy ieLibrary\main.js+x.js+x1.js+x2.js wf.js
