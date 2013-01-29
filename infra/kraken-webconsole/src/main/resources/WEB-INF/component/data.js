define(["/lib/knockout-2.1.0.debug.js", '/component/kuro.js'], function(ko, $K) {

var Data = $K.namespace("Data");

Data.ViewModel = function (data, option) {
	var self = this;
	this.self = this;

	this.items = ko.observableArray(data);

	this.canSelectMulti = ko.observable(false);
	this.selected = ko.observableArray([]);

	function bindEvent(collection) {
		$.each(collection, function(i, obj) {
			obj.onSelect = function(that, e) {
				self.select(that, e);
			}

			obj.isSelected = ko.observable(false);
		})
	}

	function observeValue(collection) {
		if(!!option) {
			if(!!option.observeKeys) {

				$.each(collection, function(i, obj) {
					$.each(option.observeKeys, function(j, key) {
						var val = obj[key];

						obj[key] = ko.observable(obj[key]);
					});
				});

			}
		}
	}

	bindEvent(this.items());
	observeValue(this.items());
}

function setPublicMethod(self) {

	self.add = function(item) {
		this.items.push(item);
	}

	self.insert = function(item, index) {
		this.items.splice(index, 0, item);
	}

	self.remove = function(item) {
		this.items.remove(item);
	}
	
	self.removeAt = function(index) {
		this.items.splice(index, 1);
	}

	self.select = function(item, e) {
		if(this.canSelectMulti() === undefined) { 
			this.canSelectMulti(false)
		}

		if(!this.canSelectMulti()) {
			if(this.selected().length > 0) {
				this.selected()[0].isSelected(false);
			}
			this.selected.splice(0, this.selected().length);
		}


		item.isSelected(true);
		this.selected.push(item);

		if(!!this.onSelect) {
			if(e) {
				this.onSelect(item, e);
			}
			else {
				this.onSelect(item);
			}
		}
	}

	self.selectAll = function(toggle) {
		$.each(this.items(), function(i, obj) {
			obj.isSelected(toggle);
		});
	}

	self.selectAt = function(idx) {
		if(idx < 0 || idx >= this.items().length) {
			console.log("out of index: " + idx)
			return;
		}
		this.select(this.items()[idx]);
	}


}

setPublicMethod(Data.ViewModel.prototype);

return Data;

});