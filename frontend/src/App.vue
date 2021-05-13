<template>
  <router-view @updateData="updateUserData" :data="data"></router-view>
</template>

<script>

export default {
  name: 'App',
  data() {
    return {
      data: null,
    }
  },
  components: {
    
  }, 
  methods: {
    updateUserData(data) {
      this.data = data
    }
  },
  emits: ['updateData']
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
