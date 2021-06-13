<template>
<v-container align-center>
    <v-container>
        <v-row>
            <v-col cols="12">
                <h1 class="main">404</h1>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" class="focus">
                <v-btn plain :to="{name: 'Profile'}">
                    Country Road, Take Me Home...
                </v-btn>
            </v-col>
        </v-row>
    </v-container>
</v-container>
</template>

<script>
export default {
    name: 'PageNotFound',
    data() {
        return {
        }
    },
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Josefin+Sans&display=swap');

.main {
    font-family: 'Josefin Sans';
    font-size: 10rem;
    text-align: center;
}

.focus {
    text-align: center;
}
</style>