function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function generate(af){
		var theUI = {
					nodes : {},
					edges: {}
		}
		$.each(af.arguments, function(index, value){
			theUI['nodes'][value.label] = {
					color: 'red',
					shape: 'dot',
					alpha: 1,
					label: value.label
				}
		})
		
		$.each(af.attackRelation, function(index, value){
			theUI['nodes'][value.attackLabel] = {
				color: 'blue',
				shape: 'square',
				alpha: 0.5,
				label: value.attackLabel
			};
			
			$.each(af.attackRelation, function(index, value){
				var formedObject = {};
				var genColor = getRandomColor();
				formedObject[value.attacked.label] = {
					directed: true,
					weight: 3,
					color: genColor
				};
				theUI['edges'][value.attackLabel] = formedObject;
				
				$.each(value.attackMembers, function(index2, value2){
					if(!theUI['edges'][value2]){
						theUI['edges'][value2] = {};
					}
					theUI['edges'][value2][value.attackLabel] = {
						directed: true,
						weight: 3,
						color: genColor
					};
				});
			})
			
		})
		
		var sys = arbor.ParticleSystem()
	    sys.parameters({stiffness:900, repulsion:2000, gravity:true, dt:0.015})
	    sys.renderer = Renderer("#sitemap")
	    sys.graft(theUI)
	}