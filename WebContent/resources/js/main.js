var sys, clickedNode, theUI;
function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function generate(af){
		theUI = {
					nodes : {},
					edges: {}
		}
		$.each(af.arguments, function(index, value){
			theUI['nodes'][value.label] = {
					color: 'red',
					shape: 'dot',
					alpha: 1,
					label: 'Arg: ' + value.label,
				}
		})
		
		$.each(af.attackRelation, function(index, value){
			theUI['nodes'][value.attackLabel] = {
				color: 'blue',
				shape: 'square',
				alpha: 1,
				label: 'Att: ' + value.attackLabel
			};
			
			$.each(af.attackRelation, function(index, value){
				var formedObject = {};
				var genColor = getRandomColor();
				formedObject[value.attacked.label] = {
					directed: true,
					weight: 10,
					color: genColor
				};
				theUI['edges'][value.attackLabel] = formedObject;
				
				$.each(value.attackMembers, function(index2, value2){
					if(!theUI['edges'][value2]){
						theUI['edges'][value2] = {};
					}
					theUI['edges'][value2][value.attackLabel] = {
						directed: false,
						weight: 5,
						color: genColor
					};
				});
			})
			
		})
		
		sys = arbor.ParticleSystem()
	    sys.parameters({stiffness:900, repulsion:2000, gravity:true, dt:0.015})
	    sys.renderer = Renderer("#sitemap")
	    sys.graft(theUI)
	}

function checkNode(node, pt){
	if (node.name == clickedNode){
		node.data.color = 'red';
	} else {
		node.data.color = '#d3d3d3';
	}
}

function checkEdge(edge, pt1, pt2){
	if (edge.source.name == clickedNode){
		edge.data.color = 'green';
		edge.target.data.color = 'blue';
	} else if (edge.target.name == clickedNode){
		edge.data.color = 'red'
		edge.source.data.color = 'red';
	} else {
		edge.data.color = '#d3d3d3';
	}
}	
function highlightNode(data){
	clickedNode = $(data).text();
	sys.eachNode(checkNode);
	sys.eachEdge(checkEdge);
	sys.graft(theUI);
}

function checkAttEdge(edge, pt1, pt2){
	if (edge.source.name == clickedNode) {
		edge.data.color = 'red';
		edge.target.data.color = 'blue';
	} else if (edge.target.name == clickedNode) {
		edge.data.color = 'green';
		edge.source.data.color = 'green';
	} else {
		edge.data.color = '#d3d3d3';
	}
}

function highlightAttack(data){
	clickedNode = $(data).text();
	sys.eachNode(checkNode);
	sys.eachEdge(checkAttEdge);
	sys.graft(theUI);
}