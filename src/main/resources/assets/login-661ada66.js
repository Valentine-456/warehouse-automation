import{l as n}from"./index-55f5978c.js";const i=n("loginStore",{state:()=>({jwt_token:""}),getters:{},actions:{async loginRequest({id_employee:e,password:o}){const r=`http://localhost:8080/login?login=${e}&password=${o}`;try{const t=await fetch(r,{method:"POST",headers:{"Content-Type":"application/json"}});if(t.ok){const s=await t.text();return this.jwt_token=s,console.log(t.headers),!0}return!1}catch(t){return console.error("Error:",t),!1}},clearStore(){this.jwt_token=""}}});export{i as u};