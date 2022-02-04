//this will serve as the main javascript file (for rendering the visualization)
//==============================================================================

//global vals & sentinel vals
var elems = {}          // var to store elements from the json
var netID = ""          // var to keep track of the current node id (IF USING DATABASE, MAKE THIS PRIMARY KEY)
var IPCollection = []  //array of IP nodes connected to the current network
var packets = []    //array of packets sent from the sIP to the dIP
var protocols = new Map([
    [0, "IP"],
    [1, "ICMP"],
    [2, "IGMP"],
    [3, "GGP"],
    [4, "IP-ENCAP"],
    [5, "ST"],
    [6, "TCP"],
    [8, "EGP"],
    [9, "IGP"],
    [12, "PUP"],
    [17, "UDP"],
    [20, "HMP"],
    [22, "XNS-IDP"],
    [27, "RDP"],
    [29, "ISO-TP4"],
    [33, "DCCP"],
    [36, "XTP"],
    [37, "DDP"],
    [38, "IDPR-CMTP"],
    [41, "IPv6"],
    [43, "IPv6-Route"],
    [44, "IPv6-Frag"],
    [45, "IDRP"],
    [46, "RSVP"],
    [47, "GRE"],
    [50, "IPSEC-ESP"],
    [51, "IPSEC-AH"],
    [57, "SKIP"],
    [58, "IPv6-ICMP"],
    [59, "IPv6-NoNxt"],
    [60, "IPv6-Opts"],
    [73, "RSPF CPHB"],
    [81, "VMTP"],
    [88, "EIGRP"],
    [89, "OSPFIGP"],
    [93, "AX.25"],
    [94, "IPIP"],
    [97, "ETHERIP"],
    [98, "ENCAP"],
    [99, "Private Encryption"],
    [103, "PIM"],
    [108, "IPCOMP"],
    [112, "VRRP"],
    [115, "L2TP"],
    [124, "ISIS"],
    [132, "SCTP"],
    [133, "FC"],
    [135, "Mobility-Header"],
    [136, "UDPLite"],
    [137, "MPLS-in-IP"],
    [138, "MANET"],
    [139, "HIP"],
    [140, "Shim6"],
    [141, "WESP"],
    [142, "ROHC"]
])
//user AJAX to retrieve elements.json
//by using XMLHttpRequest
/*
var xhttp = new XMLHttpRequest()
xhttp.onreadystatechange = function () {
    //if request is ready and everything went smoothly
    if (this.readyState == 4 && this.status == 200) {
        elems = JSON.parse(this.responseText)

        //add the elements to the graph
        graph.add(elems)
    }
}
//retrieve and send the json resource
xhttp.open("GET", "./data/elements.json", true)
xhttp.send()
*/

var graph = cytoscape({
    container: document.getElementById('cy'),

    //CONFIGURATION
    wheelSensitivity: 0.2,  //zoom scaling (how much to zoom in and out by)

    //elements in the graph
    //data fields:
    //netName = name of the network(s)
    //IPAddress = IPAddress(es) contained within that network
    //connections = any nodes that the current node connect to (used to retrieve IP addresses of connected nodes)

    // the stylesheet for the graph
    style: [
        {
            selector: 'node',
            style: {
                'background-color': 'white',
                'label': 'data(netName)',
                'color': 'white',
                'font-family': 'Cambria'
            },
        },

        {
            selector: 'edge',
            style: {
                'width': 3,
                'line-color': 'rgb(15, 230, 43)',
                'target-arrow-color': 'rgb(15, 230, 43)',
                'target-arrow-shape': 'none',
                'curve-style': 'bezier'
            }
        },

        {
            selector: '.subnet',
            style: {
                'width': 3,
                'line-color': 'rgb(255, 245, 46)',
                'target-arrow-color': 'rgb(255, 245, 46)',
                'target-arrow-shape': 'triangle',
                'curve-style': 'bezier'
            }
        }
    ],

    layout: {
        name: 'grid',
        rows: 1
    }
});

//HELPER METHODS / AUXILIARY FUNCTIONS
//toggleElement() - helper method for toggling certain elements 
function toggleElement(elementID) {
    //if element currently showing
    if (document.getElementById(elementID).style.display != 'none')
        document.getElementById(elementID).style.display = 'none'
    //otherwise element is not showing   
    else
        document.getElementById(elementID).style.display = 'block'
}

//formatIPAddress() - helper method to format the IP Addresses
//for displaying
function formatIPAddress(IPList) {
    let formattedIPAddresses = "IP Address(es): <br />"

    for (let currAddress in IPList) {
        formattedIPAddresses += IPList[currAddress] + "<br/>"
    }

    //return the formatted IP addresses
    return formattedIPAddresses
}

//displayTargets() -> helper method to display the connections a node has
//in a presentable format
function displayTargets(targets) {
    let output = ""

    //for each connection id in targets
    for (let currID in targets) {
        //retreive the corresponding target node from the graph
        let currNode = graph.getElementById(targets[currID])

        //append the network name to the output
        output += "<h1>" + currNode.data('netName') + "</h1><br />"

        //append the IP addresses
        output += formatIPAddress(currNode.data('IPAddress')) + "<hr />"
    }

    return output
}

//clearIPGraph() -> helper method to clear the graph of any
//populated nodes representing the currently clicked
//node IP address
function clearIPGraph() {
    for (let index in IPCollection) {
        let currElem = graph.getElementById(IPCollection[index])

        graph.remove(currElem)
    }

    //empty IPCollection by resetting it to an empty
    //array
    IPCollection = []
}

function plotIPs(IPAddresses, IPConnections, node, nodeX, nodeY) {
    //position to insert new nodes
    let newX = nodeX - 50
    let newY = nodeY - 50

    let dIPList = []
    //for each elem in IPConnections
    for (let index in IPConnections) {
        //get current connection network name
        let currConn = IPConnections[index]

        //use network name to retrieve subnets from the destination
        dIPList.push(graph.getElementById(currConn).data('IPAddress'))
    }

    //adding new nodes on the graph
    //with the IP addresses of the current node
    for (let index in IPAddresses) {
        //for each elem in IP addresses
        //add a node with the IP name
        graph.add([
            { data: { id: IPAddresses[index], netName: IPAddresses[index], type: 'subnet', sIP: IPAddresses[index], dIP: dIPList }, position: { x: newX, y: newY } },
            { data: { id: "address" + index, source: node.data('id'), target: IPAddresses[index] } }
        ])

        newY -= 50

        //styling
        graph.getElementById('address' + index).addClass('subnet')
    }
    //allocate temp array IPCollection with the values
    //of the IP addresses of the current node
    IPCollection = IPAddresses
}

//handleSubnet() -> helper method to check if node param is a subnet
//via the identification of its type data attribute
function handleSubnet(node, addresses, connections) {
    //display the sIP
    document.getElementById('sIPField').innerHTML = `
        <h1>sIP: `+ node.data('sIP') + "</h1>"

    //get all the addresses where packets are being sent from this sIP
    //packets = extractPacketAddresses(node.data('sIP'))

    let dIPString = '<h1>dIP: </h1>'
    //display the dIPs
    for (let index in node.data('dIP')[0]) {
        dIPString += `
            <h1>`+ node.data('dIP')[0][index] + "</h1>"
    }

    document.getElementById('dIPField').innerHTML = dIPString
}

//updateNetworkInfo() -> helper method to display general info about the clicked network
function updateNetworkInfo(networkID) {
    //get params to display + update
    let netNode = graph.getElementById(networkID)
    let netName = netNode.data('netName')
    packets = netNode.data('packets')

    //get the network info div
    let elem = document.getElementById('networkInfo')

    //update network info + packet info div
    let output = "<h1>Network Name: " + netName + "</h1><h1>Packet(s) <button id=\"showPacketButton\" onclick=\"showPackets()\"><img src=\"./images/plus.png\" width=\"20\" height=\"20\" alt=\"\"></button></h1>"
    elem.innerHTML = output
}

//showPackets() -> helper method to display packet information about the clicked network
function showPackets() {
    //get body for info display
    let elem = document.getElementById('packetInfo')

    //if elem is already visible
    if (elem.style.display == 'block') {
        //some styling
        document.getElementById('showPacketButton').innerHTML = `<img src=\"./images/plus.png\" width=\"20\" height=\"20\" alt=\"\">`

        //close the display body
        elem.style.display = 'none'
    }
    //if the elem is not visible
    else {
        //some styling
        document.getElementById('showPacketButton').innerHTML = `<img src=\"./images/minus.png\" width=\"20\" height=\"20\" alt=\"\">`

        //get packets
        let packetList = graph.getElementById(netID).data('packets')

        let output = ""
        //for each packet sent from this network
        //list out the starting subnet, destination subnet, nu
        for (let index in packetList) {
            output += `
                <h1 class=\"packets_heading\">sIP: `+ packetList[index].sIP + ` | dIP: ` + packetList[index].dIP + ` | packets: `+packetList[index].numPackets+` | protocol: ` + handleProtocol(packetList[index].protocol) +` </h1>`
        }

        elem.style.display = 'block'
        elem.innerHTML = output
    }
}

//handleProtocol() -> helper function to translate protocol num to the corresponding protocol
function handleProtocol(protocolNum){
    //check to see if the protocolNum is valid (exists in the protocol map obj)
    let protVal = protocols.get(protocolNum)
    
    //if protocolNum is not a key-value pairing in the protocol map 
    if(typeof protVal != "undefined"){
        return protVal
    }
    else{
        return -1
    }
}

//clearSubnetFields() -> helper method to clear subnet fields in the bottom overlay
//when switching to another network
function clearSubnetFields() {
    document.getElementById('sIPField').innerHTML = ""
    document.getElementById('dIPField').innerHTML = ""
}

//processJSON() -> helper method to process the JSON input from the user
//using a FileReader object
function processJSON(input) {
    //for each file in input
    for (let i = 0; i < input.files.length; i++) {
        //for the current file,
        //get the file object
        let file = input.files[i]

        //construct a new FileReader object
        //to read the contents of the JSON file
        let reader = new FileReader()

        //read the JSON file as text
        reader.readAsText(file)

        reader.onload = function () {
            //try to parse the user inputted file
            try {
                //parsing the file into JSON
                elems = JSON.parse(reader.result)

                //add the elements to the graph
                graph.add(elems)

            }
            //catching any errors & displaying a general
            //error message
            catch (error) {
                alert('Invalid JSON file/format. Try again')
            }
        }

        //when the FileReader object fails to load
        //alert out the error
        reader.onerror = function () {
            alert(reader.error)
        }
    }
}

//EVENT HANDLING
//general press of the key event handler
//used to designate keys for certain actions (EX: space for resetting zoom)
window.addEventListener("keydown", function resetZoom(event) {
    switch (event.key) {
        //for zoom
        //press space to reset the zoom on the graph
        case ' ':
            graph.reset()
            break
        //for clearing the IP addresses + info off the graph
        //clean-up purposes
        case 'Escape':
            clearIPGraph()
            this.document.getElementById('networkInfo').innerHTML = ''
            this.document.getElementById('packetInfo').innerHTML = ''
    }
})


//for node information
//upon being clicked
graph.on('click', 'node', function (event) {
    //get info regarding the clicked node
    let netName = this.data('netName')

    let IPAddresses = this.data('IPAddress')
    let IPConnections = this.data('connections')
    //packets = this.data('packets')

    //clicked node's position information
    let nodeX = this.position('x')
    let nodeY = this.position('y')



    //call helper method plotIPs to help plot the
    //subnets belonging to the currently clicked node
    //if clicked node is not a subnet
    if (this.data('type') != 'subnet') {
        netID = this.data('id')

        clearIPGraph()

        clearSubnetFields()
        plotIPs(IPAddresses, IPConnections, this, nodeX, nodeY)

        //call helper method to display general network information
        updateNetworkInfo(netID)
        document.getElementById('packetInfo').style.display = 'none'
    }
    else {
        //if subnet, then display connections pertaining to that subnet
        handleSubnet(this, IPAddresses, IPConnections)
    }

})

