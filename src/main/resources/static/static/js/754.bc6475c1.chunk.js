"use strict";(self.webpackChunkfrontend=self.webpackChunkfrontend||[]).push([[754],{754:(e,t,s)=>{s.r(t),s.d(t,{default:()=>l});var r=s(43),a=s(213);var c=s(579);const i=function(e){let{searchTerm:t,handleSearch:s,selectedMarket:r,handleMarketFilter:a}=e;return(0,c.jsxs)("div",{className:"search-and-buttons",children:[(0,c.jsxs)("div",{className:"button-group",children:[(0,c.jsx)("button",{className:"custom-button",onClick:()=>alert("\uc5b4\ud734 \ubc14\ubcf4 \u3149\u3149"),children:"\ub204\ub974\uba74 \ubc14\ubcf4"}),(0,c.jsx)("button",{className:"custom-button",onClick:()=>alert("\ub098\ud63c\uc790 \ub178\ub294\uc911"),children:"\uc5ec\uae30\ub2e4 \ubb34\uc2a8\uae30\ub2a5\ub2ec\uc9c0"}),(0,c.jsx)("button",{className:"custom-button",onClick:()=>alert("Button 3 Clicked!"),children:"Button 3"})]}),(0,c.jsxs)("div",{className:"search-and-filter",children:[(0,c.jsx)("input",{type:"text",placeholder:"Search by Korean Name...",value:t,onChange:s,className:"search-input"}),(0,c.jsxs)("select",{value:r,onChange:a,className:"market-select",children:[(0,c.jsx)("option",{value:"all",children:"All Markets"}),(0,c.jsx)("option",{value:"krw",children:"KRW Market"}),(0,c.jsx)("option",{value:"btc",children:"BTC Market"}),(0,c.jsx)("option",{value:"usdt",children:"USDT Market"})]})]})]})};const n=function(e){let{title:t,items:s,btcPrice:a,handleSortByName:i,handleSortByPrice:n}=e;const[l,o]=(0,r.useState)("asc"),[d,p]=(0,r.useState)("asc"),[h,m]=(0,r.useState)("asc");return(0,c.jsxs)("div",{className:"market-list",children:[(0,c.jsx)("div",{className:"market-list-header",children:(0,c.jsxs)("h3",{children:[t," : ",s.length," \ud56d\ubaa9"]})}),(0,c.jsxs)("div",{className:"market-items-container",children:[(0,c.jsxs)("div",{className:"market-item",style:{display:"grid",gridTemplateColumns:"2fr 1fr 1fr",gap:"10px",alignItems:"center"},children:[(0,c.jsx)("div",{className:"market-name",style:{borderRight:"1px solid #ccc",textAlign:"center"},children:(0,c.jsxs)("button",{className:`sort-button ${l}`,onClick:()=>{const e="asc"===l?"desc":"asc";o(e),i(e)},children:["\uc774\ub984 \uc815\ub82c (","asc"===l?"\u25b2":"\u25bc",")"]})}),(0,c.jsx)("div",{className:"market-code",style:{borderRight:"1px solid #ccc",textAlign:"center"},children:(0,c.jsxs)("button",{className:`sort-button ${d}`,onClick:()=>{const e="asc"===d?"desc":"asc";p(e),i(e)},children:["\ucf54\uc778 \uc815\ub82c (","asc"===d?"\u25b2":"\u25bc",")"]})}),(0,c.jsx)("div",{className:"market-price",style:{textAlign:"center"},children:(0,c.jsxs)("button",{className:`sort-button ${h}`,onClick:()=>{const e="asc"===h?"desc":"asc";m(e),n(e)},children:["\uac00\uaca9 \uc815\ub82c (","asc"===h?"\u25b2":"\u25bc",")"]})})]}),s.map(((e,s)=>(0,c.jsxs)("div",{className:"market-item",style:{display:"grid",gridTemplateColumns:"2fr 1fr 1fr",gap:"10px",alignItems:"center"},children:[(0,c.jsxs)("div",{className:"market-name",style:{borderRight:"1px solid #ccc",textAlign:"center"},children:[e.korean_name," ",(0,c.jsx)("br",{}),"(",e.english_name,")"]}),(0,c.jsx)("div",{className:"market-code",style:{borderRight:"1px solid #ccc",textAlign:"center"},children:e.market}),(0,c.jsxs)("div",{className:"market-price",style:{textAlign:"center"},children:[e.trade_price?e.trade_price+("KRW Market"===t?" \uc6d0":""):"Price not available","BTC Market"===t&&(0,c.jsx)("div",{style:{fontSize:"10px"},children:(a*e.trade_price).toLocaleString().replace(/\..*$/,"")+" \uc6d0"})]})]},s)))]})]})};const l=function(){const[e,t]=(0,r.useState)(""),[s,l]=(0,r.useState)("all"),[o,d]=(0,r.useState)({krwList:[],btcList:[],usdtList:[]}),[p,h]=(0,r.useState)(0);(0,r.useEffect)((()=>{(async()=>{const e=await(async()=>{try{return(await a.A.get("/api/upbit/priceList")).data}catch(e){throw console.error("Failed to fetch Upbit list:",e),e}})();x(e)})()}),[e,s]),(0,r.useEffect)((()=>{const e=new EventSource(`${window.location.origin}/api/sse/price`),t=setInterval((()=>{fetch(`${window.location.origin}/api/sse/ping`).catch(console.error)}),1e4);return e.addEventListener("price",(e=>{const t=JSON.parse(e.data);"KRW-BTC"===t.code&&h(t.trade_price),b(t)})),()=>{e.close(),clearInterval(t)}}),[]);const m=(e,t)=>{let s=Number(t);return s>1e3||s<=1e3&&s>1?s:s<1?t:"Price not available"},u=(e,t)=>{d((s=>({...s,[e]:[...s[e]].sort(((e,s)=>"asc"===t?e.korean_name.localeCompare(s.korean_name):s.korean_name.localeCompare(e.korean_name)))})))},k=(e,t)=>{d((s=>({...s,[e]:[...s[e]].sort(((e,s)=>"asc"===t?parseFloat(e.price.replace(/,/g,""))-parseFloat(s.price.replace(/,/g,"")):parseFloat(s.price.replace(/,/g,""))-parseFloat(e.price.replace(/,/g,""))))})))},x=e=>{let t=[],s=[],r=[];const{COIN:a,PRICE:c}=e;for(let i in a){const e=a[i].market,n=c.find((t=>t.market===e));n&&(a[i].price=m(0,n.trade_price),a[i].prev_closing_price=m(n.prev_closing_price),a[i].priceChangeClass=v(n.trade_price,n.prev_closing_price),"KRW-BTC"===e&&h(a[i].price)),g(a[i],e)&&(e.startsWith("KRW-")?t.push(a[i]):e.startsWith("BTC-")?s.push(a[i]):r.push(a[i]))}d({krwList:t,btcList:s,usdtList:r})},g=(t,r)=>t.korean_name.toLowerCase().includes(e)&&("all"===s||"krw"===s&&r.startsWith("KRW-")||"btc"===s&&r.startsWith("BTC-")||"usdt"===s&&r.startsWith("USDT-")),b=e=>{d((t=>{const s=t.krwList.map((t=>{if(t.market===e.code){const s=t.price,r=e.trade_price,a=r>s?"price-up change":"price-down change";return setTimeout((()=>{d((t=>({...t,krwList:t.krwList.map((t=>t.market===e.code?{...t,priceChangeClass:a.replace(" change","")}:t))})))}),300),{...t,price:m(),priceChangeClass:a}}return t}));return{...t,krwList:s}}))},v=(e,t)=>e>t?"price-up":e<t?"price-down":"price-stable";return(0,c.jsxs)("div",{children:[(0,c.jsx)(i,{searchTerm:e,handleSearch:e=>t(e.target.value.toLowerCase()),selectedMarket:s,handleMarketFilter:e=>l(e.target.value)}),(0,c.jsxs)("div",{className:"market-lists",children:[(0,c.jsx)(n,{title:"KRW Market",items:o.krwList,btcPrice:p,handleSortByName:e=>u("krwList",e),handleSortByPrice:e=>k("krwList",e)}),(0,c.jsx)(n,{title:"BTC Market",items:o.btcList,btcPrice:p,handleSortByName:e=>u("btcList",e),handleSortByPrice:e=>k("btcList",e)}),(0,c.jsx)(n,{title:"USDT Market",items:o.usdtList,btcPrice:p,handleSortByName:e=>u("usdtList",e),handleSortByPrice:e=>k("usdtList",e)})]})]})}}}]);
//# sourceMappingURL=754.bc6475c1.chunk.js.map