<template>
  <router-view :data="data"></router-view>
</template>

<script>
import jsonData from '../data.json'

export default {
  name: 'App',
  data() {
    return {data: jsonData}
  },
  components: {
    
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
