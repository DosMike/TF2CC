var Request = (()=>{

const _request = (method, url, header, data) => {
	method = method.toUpperCase();
	let r = new XMLHttpRequest();
	return new Promise((resolve, reject)=>{
		r.addEventListener('load', ()=>{
			if (r.status >= 200 && r.status < 300) {
				resolve(r);
			} else {
				reject(r);
			}
		});
		r.addEventListener('error', ()=>{
			reject(r);
		});

		r.open(method, url, true);
		r.overrideMimeType("text/plain; charset=UTF-8");

		let type = typeof(data) === 'undefined' ? 'Undefined' : data.__proto__.constructor.name;
		if (type === 'HTMLFormElement') {
			data = new FormData(data);
		} else if (type === 'Object') {
			let d = new URLSearchParams();
			Object.keys(data).forEach(key=>
				d.set(key, data[key])
			);
			data = d;
		} else if (type !== 'FormData' && type !== 'URLSearchParams') {
			data = null;
		}

		if (typeof(header) !== 'undefined') {
			Object.keys(header).forEach(key=>
				r.setRequestHeader(key,header[key])
			);
		}
		console.log('sending data',method,data);
		if (method == 'POST' || method == 'PUT')
			r.send(data);
		else
			r.send();
	});
}
const _formAuto = (form, header) => {
	if (['post','put'].includes(form.method.toLowerCase())) {
		console.log('Post from Form');
		return _request(form.method, form.action, header, form);
	} else {
		console.log('Get from Form');
		let url = new URL(form.action);
		let formData = new URLSearchParams();
		for (const [key, value] of url.searchParams.entries()) {
			formData.append(key,value);
		}
		for (const [key, value] of new FormData(form).entries()) {
			formData.append(key,value);
		}
		let target = url.origin+url.pathname+'?'+formData.toString();
		return _request(form.method, target, header);
	}
}

function JSRequest() { }
JSRequest.POST = (url, header, data) => _request('POST', url, header, data);
JSRequest.PUT = (url, header, data) => _request('PUT', url, header, data);
JSRequest.GET = (url, header) => _request('GET', url, header);
JSRequest.HEAD = (url, header) => _request('HEAD', url, header);
JSRequest.DELETE = (url, header) => _request('DELETE', url, header);
JSRequest.form = (form, header) => _formAuto(form, header);
return JSRequest;

})()