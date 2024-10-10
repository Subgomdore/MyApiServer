"use strict";(self.webpackChunkfrontend=self.webpackChunkfrontend||[]).push([[401],{401:(e,t,s)=>{s.r(t),s.d(t,{default:()=>l});var a=s(43),r=s(213);var i=s(579);const l=function(){const[e,t]=(0,a.useState)(""),[s,l]=(0,a.useState)("all"),[n,c]=(0,a.useState)({krwList:[],btcList:[],usdtList:[]}),[d,o]=(0,a.useState)({}),[m,h]=(0,a.useState)(0);(0,a.useEffect)((()=>{(async()=>{try{return(await r.A.get("/api/upbit/priceList")).data}catch(e){throw console.error("Failed to fetch Upbit list:",e),e}})().then((t=>{k(t,e,s)}))}),[e,s]);const k=(e,t,s)=>{let a=[],r=[],i=[];const l=e.COIN,n=e.PRICE;for(let c in l){const e=l[c].market;for(let t in n)if(n[t].market===e){let e=Number(n[t].trade_price).toFixed(10).replace(/\.?0+$/,"");l[c].price=e}e.startsWith("KRW-")&&l[c].korean_name.toLowerCase().includes(t)&&("all"===s||"krw"===s)?a.push(l[c]):e.startsWith("BTC-")&&l[c].korean_name.toLowerCase().includes(t)&&("all"===s||"btc"===s)?r.push(l[c]):!l[c].korean_name.toLowerCase().includes(t)||"all"!==s&&"usdt"!==s||i.push(l[c])}console.log(a),c({krwList:a,btcList:r,usdtList:i})};return(0,i.jsxs)("div",{children:[(0,i.jsxs)("div",{className:"search-and-buttons",children:[(0,i.jsxs)("div",{children:[(0,i.jsx)("button",{onClick:()=>alert("Button 1 Clicked!"),children:"Button 1"}),(0,i.jsx)("button",{onClick:()=>alert("Button 2 Clicked!"),children:"Button 2"}),(0,i.jsx)("button",{onClick:()=>alert("Button 3 Clicked!"),children:"Button 3"})]}),(0,i.jsxs)("div",{style:{display:"flex",alignItems:"center"},children:[(0,i.jsx)("input",{type:"text",placeholder:"Search by Korean Name...",value:e,onChange:e=>{const a=e.target.value.toLowerCase();t(a),k(n,a,s)}}),(0,i.jsxs)("select",{value:s,onChange:t=>{const s=t.target.value;l(s),k(n,e,s)},children:[(0,i.jsx)("option",{value:"all",children:"All Markets"}),(0,i.jsx)("option",{value:"krw",children:"KRW Market"}),(0,i.jsx)("option",{value:"btc",children:"BTC Market"}),(0,i.jsx)("option",{value:"usdt",children:"USDT Market"})]})]})]}),(0,i.jsxs)("div",{className:"market-lists",children:[(0,i.jsxs)("div",{className:"market-list",children:[(0,i.jsxs)("h3",{children:["KRW Market : ",n.krwList.length," \ud56d\ubaa9"]}),n.krwList.map(((e,t)=>(0,i.jsxs)("div",{className:"market-item",children:[(0,i.jsxs)("div",{className:"market-name",children:[e.korean_name," (",e.english_name,")"]}),(0,i.jsx)("div",{className:"market-code",children:e.market}),(0,i.jsx)("div",{className:"market-price",children:e.price?parseInt(e.price).toLocaleString()+" \uc6d0":"Price not available"})]},t)))]}),(0,i.jsxs)("div",{className:"market-list",children:[(0,i.jsxs)("h3",{children:["BTC Market : ",n.btcList.length," \ud56d\ubaa9"]}),n.btcList.map(((e,t)=>(0,i.jsxs)("div",{className:"market-item",children:[(0,i.jsxs)("div",{className:"market-name",children:[e.korean_name," (",e.english_name,")"]}),(0,i.jsx)("div",{className:"market-code",children:e.market}),(0,i.jsxs)("div",{className:"market-price",children:[e.price?parseInt(e.price).toLocaleString()+" \uc6d0":"Price not available",(0,i.jsx)("div",{style:{fontSize:"10px",color:"midnightblue"},children:m&&d[e.market]?(m*d[e.market]).toLocaleString().replace(/\..*/,"")+" \uc6d0":""})]})]},t)))]}),(0,i.jsxs)("div",{className:"market-list",children:[(0,i.jsxs)("h3",{children:["USDT Market : ",n.usdtList.length," \ud56d\ubaa9"]}),n.usdtList.map(((e,t)=>(0,i.jsxs)("div",{className:"market-item",children:[(0,i.jsxs)("div",{className:"market-name",children:[e.korean_name," (",e.english_name,")"]}),(0,i.jsx)("div",{className:"market-code",children:e.market}),(0,i.jsx)("div",{className:"market-price",children:d&&d[e.market]?d[e.market].toLocaleString():"Price not available"})]},t)))]})]})]})}}}]);
//# sourceMappingURL=401.a34658a2.chunk.js.map