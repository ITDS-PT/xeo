/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.grid.filter.ObjectFilter = Ext.extend(Ext.grid.filter.Filter, {
    /**
     * @cfg {Boolean} defaultValue
     * The default value of this filter (defaults to false)
     */
    defaultValue: false,
    /**
     * @cfg {String} yesText
     * The text displayed for the "Yes" checkbox
     */
    yesText: 'Seleccionar valores',
    /**
     * @cfg {String} noText
     * The text displayed for the "No" checkbox
     */
    noText: 'Não',

	init: function(){
	    var gId = Ext.id();
			this.options = [
				new Ext.menu.Item({text: this.yesText, group: gId })
	    ];
		this.menu.add(this.options[0]);
		for(var i=0; i<this.options.length; i++) {
			this.options[i].on('click', this.fireClick, this);
			this.options[i].on('checkchange', this.fireUpdate, this);
		}
	},
	
	isActivatable: function() {
		return true;
	},
	
	fireClick: function() {
		this.lookupCommand();
	},
	fireUpdate: function() {		
		this.fireEvent("update", this);			
		this.setActive(true);
	},
	
	setValue: function(value) {
		this.options[value ? 0 : 1].setChecked(true);
	},
	
	getValue: function() {
		return this.options[0].checked;
	},
	
	serialize: function() {
		var args = {active:this.active, type: 'object', value: this.getValue()};
		this.fireEvent('serialize', args, this);
		return args;
	},
	
	validateRecord: function(record) {
		return record.get(this.dataIndex) == this.getValue();
	}
});
