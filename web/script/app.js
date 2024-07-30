(()=>{

    // ========== globals ==========

    let state = {
        accountid: '',
        code: 'deadbeef',
        session: '',
        members: [],
        regions: [],
        maps: [],
        servercounts: []
    }
    // delayed init
    let sendCmd = (r) => {};
    let socket = null;

    // regex from http://detectmobilebrowsers.com/
    const isMobile = () =>
        (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(navigator.userAgent || navigator.vendor || window.opera) ||
         /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test((navigator.userAgent || navigator.vendor || window.opera).substr(0, 4)));

    // ========== socket stuff ==========


    let lastToast = Date.now();
    const canControl = () => {
        let now = Date.now();
        if (!state.accountid) {
            if (now-lastToast >= 3000) {
                lastToast = now;
                alerty.toasts('You need to be logged in to do change settings!')
            }
            return false;
        } else if (state.members.length == 0 || state.accountid != state.members[0].accountid) {
            if (now-lastToast >= 3000) {
                lastToast = now;
                alerty.toasts('Only the lobby owner can change settings!')
            }
            return false;
        } else if (document.querySelectorAll('main .lobby .ready').length == state.members.length) {
            if (now-lastToast >= 3000) {
                lastToast = now;
                alerty.toasts('You can\'t change settings while queued!')
            }
            return false;
        }
        return true;
    }

    const setupSocket = ()=>{
        socket = new WebSocket("wss://"+document.location.host+":8088")
        socket.addEventListener("open", (event) => {
            sendCmd = (s)=>{ console.log("WebSocket TX:",s); socket.send(s) }
            sendCmd("HELO "+state.session)
        })
        socket.addEventListener("message", (event) => {
            console.log("WebSocket RX:", event.data);
            sockRx(event.data)
        })
        socket.addEventListener("error", (event) => {
            console.log("WebSocket error:", event);
        })
    }

    const sockRx = (data) => {
        const reply = data.replace(/\s{2,}/g, ' ').split(' ');
        if (reply[0] == 'ERROR') {
            let msg = data.slice(6);
            alerty.alert(msg, {title: 'Error'})
        } else if (reply[0] == 'LIST') {
            lobbyLoad(reply.slice(1))
        } else if (reply[0] == 'JOIN') {
            lobbyAdd(reply[1])
        } else if (reply[0] == 'LEAVE') {
            lobbyRemove(reply[1])
        } else if (reply[0] == 'AFK') {
            lobbyReady(reply[1], '0')
            let ready_img = document.querySelector('main .lobby [data-accountid="'+reply[1]+'"]');
            if (ready_img) ready_img.classList.add('afk')
        } else if (reply[0] == 'CLOSE') {
            state.members = []
            state.code = ''
            updateLobby()
            alerty.alert('Uh you should not normally see this, so we\'ll just reload the page...', {title:'Lobby was closed!'}, ()=>document.location.reload())
        } else if (reply[0] == 'READY') {
            lobbyReady(reply[1], reply[2])
        } else if (reply[0] == 'CONFIG' && reply.length > 2) {
            updateSelection(reply[1], reply.slice(2).filter(x=>!!x))
        } else if (reply[0] == 'CONNECT') {
            doConnect(reply[1])
        } else if (reply[0] == 'SYSNOTIF') {
            alerty.alert(data.slice(9), {title: 'System Message'})
        }
    }

    const lobbyReady = (account, ready) => {
        let ready_img = document.querySelector('main .lobby [data-accountid="'+account+'"]');
        if (ready == "0") {
            ready_img.classList.remove('ready')
            playSound('ready_down')
            document.querySelector('.queueoverlay').classList.remove('show')
        } else {
            ready_img.classList.add('ready')
            if (document.querySelectorAll('main .lobby .ready').length == state.members.length) {
                playSound('queue')
                document.querySelector('.queueoverlay').classList.add('show')
            } else {
                playSound('ready_up')
            }
        }
    }
    const lobbyLoad = (args) => {
        document.querySelector('.queueoverlay').classList.remove('show')
        document.querySelector('.lobbyctrl button#ready').classList.remove('in')
        state.code = args[0];
        state.members = args.slice(1).map(m=>{return {accountid: m, username: 'Loading...', avatar: 'assets/avatar_blank.png'}})
        updateLobby()
        Request.GET('action.php?do=usertags&'+(args.slice(1).map(m=>'ids[]='+m).join('&')), {}).then((req)=>{
            let jason = JSON.parse(req.responseText)
            state.members = state.members.map(m=>jason.find(r=>m.accountid == r.accountid))
            updateLobby()

            if (canControl()) {
                loadCookie()
                sendCmd("CONFIG + "+state.regions.join(" ")+" "+state.maps.join(" "))
            } else {
                sendCmd('CONFIG SYNC')
            }
        })
    }
    const lobbyAdd = (account) => {
        let indexOf = -1;
        let user = {accountid: account, username: 'Loading...', avatar: 'assets/avatar_blank.png'}
        if (!state.members.find(m=>m.accountid == account)) {
            state.members.push(user)
            updateLobby()
        }
        Request.GET('action.php?do=usertags&ids[]='+account, {}).then((req)=>{
            let response = JSON.parse(req.responseText)[0]
            let index = -1;
            for (let i = 0; i < state.members.length; i ++) if (state.members[i].accountid == response.accountid) index = i;
            if (index >= 0) {
                state.members[index] = response
            }
            updateLobby()
        })
    }
    const lobbyRemove = (account) => {
        state.members = state.members.filter(m=>m.accountid != account)
        updateLobby()
    }
    const kickPlayer = (account) => {
        sendCmd('KICK '+account)
    }

    const updateLobby = () => {
        let lobby = document.querySelector('main .lobby')
        let i=0;
        for (; i<state.members.length; i++) {
            let container = document.createElement('div')
            let img = document.createElement('img')
            img.src = state.members[i].avatar
            img.title = state.members[i].username
            container.dataset.accountid = state.members[i].accountid
            container.classList.add('joined')
            container.appendChild(img)
            lobby.replaceChild(container, lobby.children[i])
            container.addEventListener('click', (e)=>askKickMember(container))
        }
        for (; i<6; i++) {
            let container = document.createElement('div')
            let img = document.createElement('img')
            img.src = 'assets/avatar_blank.png'
            img.title = 'Not connected'
            container.appendChild(img)
            lobby.replaceChild(container, lobby.children[i])
        }
    }

    const joinLobby = (code) => {
        code = code.trim()
        if (code.length != 6) {
            alerty.alert('Invalid lobby code, should be 6 digits', {title: 'Invalid Code'})
        } else {
            sendCmd("JOIN "+code.trim())
        }
    }
    const leaveLobby = () => {
        sendCmd("LEAVE")
    }

    const updateSelection = (kind, values) => {
        let maps = Array.from(document.querySelectorAll('.mapgroup input'));
        let regions = Array.from(document.querySelectorAll('.regions input'));
        if (kind == '=') {
            maps.forEach(m=>{
                let picked = values.includes(m.value)
                if (m.checked != picked) {
                    m.checked = picked; toggleMap(m, false)
                }
            })
            regions.forEach(r=>{
                let picked = values.includes(r.value)
                if (r.checked != picked) {
                    r.checked = picked; toggleRegion(r, false)
                }
            })
        } else if (kind == '+') {
            maps.forEach(m=>{
                let picked = values.includes(m.value)
                if (picked && !m.checked) {
                    m.checked = true; toggleMap(m, false)
                }
            })
            regions.forEach(r=>{
                let picked = values.includes(r.value)
                if (picked && !r.checked) {
                    r.checked = true; toggleRegion(r, false)
                }
            })
        } else if (kind == '-') {
            maps.forEach(m=>{
                let picked = values.includes(m.value)
                if (picked && m.checked) {
                    m.checked = false; toggleMap(m, false)
                }
            })
            regions.forEach(r=>{
                let picked = values.includes(r.value)
                if (picked && r.checked) {
                    r.checked = false; toggleRegion(r, false)
                }
            })
        } else {
            console.log("Invalid CONFIG action:",kind)
        }
    }

    const doConnect = (ipport) => {
        document.querySelector('.queueoverlay').classList.remove('show')
        document.querySelector('.lobbyctrl button#ready').classList.remove('in')
        Array.from(document.querySelectorAll('main .lobby > div')).forEach(l => l.classList.remove('ready'))
        window.open(window.location.protocol+'//'+window.location.host+'/connect/'+ipport, '_blank')
        playSound('redirect')
    }

    // ========== cookies ==========

    const loadCookie = () => {
        let cookies = {};
        document.cookie.split(';').map(x=>x.trimStart().split('=')).forEach(e=>cookies[e[0]]=e[1]);

        if ('tf2cc' in cookies) {
            state.session = cookies['tf2cc'] // web session cookie for backend linking with steam account
        }

        if ('remember_maps' in cookies) {
            let picks = cookies['remember_maps'].split(',');
            document.querySelectorAll('.mapgroup input, .mapchoice input').forEach(x=>{
                if ((x.checked = picks.includes(x.value)))
                    toggleMap(x, false)
            })
            // update folds
            Array.from(document.querySelectorAll('.mapgroup input')).forEach(x=>toggleMap(x, false))
        } else {
            let defaultGroups = ['c:attackdefend','c:controlpoint','c:capturetheflag','c:kingofthehill','c:payload']
            Array.from(document.querySelectorAll('.mapgroup input')).forEach(x=>{
                x.checked = defaultGroups.includes(x.value)
                toggleMap(x, false)
            })
        }

        if ('remember_regions' in cookies) {
            let regions = cookies['remember_regions'].split(',')
            state.regions = regions
            Array.from(document.querySelectorAll('.regions input')).forEach(x=>{x.checked = regions.includes(x.value)})
        } else {
            state.regions = []
            Array.from(document.querySelectorAll('.regions input')).forEach(x=>{x.checked = true; state.regions.push(x.value)})
        }
    }
    const storeCookie = () => {
        if (!canControl()) return;

        let picks = [];
        Array.from(document.querySelectorAll('.mapgroup input')).forEach(x=>{
            if (x.checked) {
                picks.push(x.value)
            } else {
                picks = picks.concat(Array.from(x.closest('.mapgroup').nextElementSibling.querySelectorAll('input')).filter(x=>x.checked).map(x=>x.value))
            }
        })

        let regions = [];
        Array.from(document.querySelectorAll('.regions input')).forEach(x=>{
            if (x.checked) regions.push(x.value)
        })

        let exp = new Date();
        exp.setTime(exp.getTime()+7*24*3600000);
        document.cookie = "remember_maps="+picks.join(',')+"; samesite=strict; expires="+exp.toUTCString()
        document.cookie = "remember_regions="+regions.join(',')+"; samesite=strict; expires="+exp.toUTCString()
    }

    // ========== ui ==========

    const toggleMap = (map, send=true) => {
        let foldGroup=false
        let group=null
        let configAdd = [];
        let configRem = [];

        if (map.value.startsWith('c:')) {
            // go from input->h2->label, next sibling -> inputs
            group = map.closest('.mapgroup').nextElementSibling;
            group.querySelectorAll('input').forEach(x=>{
                if (x.checked != map.checked) {
                    x.checked = map.checked
                    if (map.checked) configAdd.push(x.value)
                    else configRem.push(x.value)
                }
            })
            foldGroup = !map.checked
        } else {
            if (map.checked) configAdd.push(map.value)
            else configRem.push(map.value)
            // compute group state with parent selected inputs / parent inputs
            let maps = 0
            let selected = 0
            group = map.closest('ul');
            Array.from(group.querySelectorAll('input')).forEach(x=>{maps++; if(x.checked) selected++})
            let heading = group.previousElementSibling.querySelector('input')
            heading.checked = (maps == selected)
            heading.indeterminate = (selected > 0 && selected < maps)
            foldGroup = (selected == 0)
        }
        if (foldGroup) group.classList.add('fold');
        else group.classList.remove('fold');

        if (configAdd.length > 0) {
            state.maps = state.maps.concat(configAdd.filter(e=>!state.maps.includes(e)))
            if (canControl() && send) sendCmd("CONFIG + "+configAdd.join(" "))
        } else if (configRem.length > 0) {
            state.maps = state.maps.filter(e=>!configRem.includes(e))
            if (canControl() && send) sendCmd("CONFIG - "+configRem.join(" "))
        }
        if (send) playSound('click')
    }
    const toggleRegion = (region, send=true) => {
        if (state.regions.includes(region.value)) {
            var index = state.regions.indexOf(region.value)
            state.regions.splice(index, 1)
            if (canControl() && send) sendCmd("CONFIG - "+region.value)
        }else {
            state.regions.push(region.value)
            if (canControl() && send) sendCmd("CONFIG + "+region.value)
        }
        servercount_maps()
        playSound('click')
    }
    const toggleReady = () => {
        if (isMobile()) {
            playSound('click_off')
            alerty.toasts('You can not ready up on mobile')
        }
        let readybn = document.querySelector('.lobbyctrl button#ready')
        let myimg = document.querySelector('.lobby [data-accountid="'+state.accountid+'"]')
        readybn.classList.toggle('in')
        if (readybn.classList.contains('in')) {
            myimg.classList.add('ready')
            sendCmd("READY 1")
        } else {
            myimg.classList.remove('ready')
            sendCmd("READY 0")
        }
    }
    const playSound = (name) => {
        let elem = document.querySelector('#soundboard [name="'+name+'"]');
        if (elem) {
            elem.currentTime = 0.0
            elem.play()
        }
    }

    const servercount_maps = () => {
        // for maps
        // servercounts.maps is structured map -> [region -> count]
        //   map value to sum(count for rg,count in value if rg in selection)
        console.log(state.servercounts.maps)
        let max = Math.max(...Object.values(state.servercounts.maps)
            .map(x=>Object.entries(x)).map(x=>x.reduce((accu,[rg,cnt])=>accu+(state.regions.includes(rg)?cnt:0), 0))) || 1
        Array.from(document.querySelectorAll('.mapchoice input')).forEach(map=>{
            let name = map.value;
            let servers = 0;
            if ((Object.keys(state.servercounts.maps).includes(name))){
                servers = Object.entries(state.servercounts.maps[name])
                    .filter(([rg,_])=>state.regions.includes(rg))
                    .reduce((accu,[_,cnt])=>accu+cnt,servers)
            }
            let counter = map.parentElement.querySelector('.servercount')
            counter.innerText = servers+" Servers"
            let percent = servers / max
            let hue = (percent * 120).toFixed(0)
            percent = (percent * 85 + 15).toFixed(0)+'%'
            counter.style.backgroundImage = 'linear-gradient(270deg, hsl('+hue+' 60% 33%) 0, hsl('+hue+' 90% 66%) '+percent+', #0000 '+percent+', #0000 100%)'
        })
    }
    const servercount_regions = () => {
        // for regions
        let max = Math.max(...Object.values(state.servercounts.regions))
        Array.from(document.querySelectorAll('.regions input')).forEach(reg=>{
            let name = reg.value;
            let servers = (Object.keys(state.servercounts.regions).includes(name)) ? state.servercounts.regions[name] : 0;
            let counter = reg.parentElement.querySelector('.servercount')
            counter.innerText = servers+" Servers"
            let percent = servers / max
            let hue = (percent * 120).toFixed(0)
            percent = (percent * 85 + 15).toFixed(0)+'%'
            counter.style.backgroundImage = 'linear-gradient(270deg, hsl('+hue+' 60% 33%) 0, hsl('+hue+' 90% 66%) '+percent+', #0000 '+percent+', #0000 100%)'
        })
    }
    const servercount = () => {
        Request.GET('action.php?do=servercount').then((req)=>{
            let counts = JSON.parse(req.responseText)
            state.servercounts = counts
            servercount_maps()
            servercount_regions()
        })
        window.setTimeout(()=>servercount(), 120000)
    }

    const showLobbyCode = () => {
        playSound('click')
        alerty.alert('Copy the lobby code above and send it to other players to let them join your lobby.', {title: 'Lobby Code: '+state.code})
    }
    const askLobbyCode = () => {
        playSound('click')
        alerty.prompt('Join someone else\'s lobby by getting their lobby code and pasting it below.', {title: 'Join Lobby', okLabel: 'Join', inputPlaceholder: 'Put lobby code here'}, (c)=>{joinLobby(c)})
    }
    const askLobbyLeave = () => {
        playSound('click')
        alerty.confirm('Are you sure you want to leave the lobby?', {title: 'Leave Lobby', okLabel: 'Leave'}, ()=>{leaveLobby()})
    }
    const askKickMember = (x) => {
        if (x.dataset.accountid && canControl()) {
            if (x.dataset.accountid == state.accountid) {
                alerty.toasts("You can't kick yourself")
                playSound('click_off')
            } else {
                let name = x.querySelector('img').title
                alerty.confirm("Do you really want to kick "+name+" from the lobby?", {title: 'Kick Player', okLabel: 'Kick'}, ()=>kickPlayer(x.dataset.accountid));
                playSound('click')
            }
        }
    }

    // ========== loader ==========

    document.addEventListener('DOMContentLoaded', ()=>{
        Array.from(document.querySelectorAll('.mapgroup input, .mapchoice input')).forEach(x=>x.addEventListener('click',e=>{
            if (canControl()) {toggleMap(x); storeCookie()}
            else { e.preventDefault(); e.stopImmediatePropagation(); }
        }))
        Array.from(document.querySelectorAll('.regions input')).forEach(x=>x.addEventListener('click',e=>{
            if (canControl()) {toggleRegion(x); storeCookie()}
            else { e.preventDefault(); e.stopImmediatePropagation(); }
        }))
        let logout = document.getElementById('logout')
        if (logout) {
            state.accountid = logout.dataset.accountid
            document.querySelector('.lobbyctrl button#ready').addEventListener('click', (e)=>{toggleReady()})
            document.querySelector('.lobbyctrl2 button#invite').addEventListener('click', (e)=>{showLobbyCode()});
            document.querySelector('.lobbyctrl2 button#join').addEventListener('click', (e)=>{askLobbyCode()});
            document.querySelector('.lobbyctrl2 button#leave').addEventListener('click', (e)=>{askLobbyLeave()});
            loadCookie()
            setupSocket()
        } else {
            Array.from(document.querySelectorAll('.mapgroup input, .mapchoice input, .regions input')).forEach(x=>{x.style.display = 'none'; x.checked = true;})
        }
        window.setTimeout(()=>servercount(), 3000)
    })

    //function _exports() {}
    //_exports.rxmsg = (s) => sockRx(s)
    //return _exports
})()