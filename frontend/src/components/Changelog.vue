<template>
    <v-sheet class="pa-5">
        <h3 class="my-5">Changelog</h3>
        <v-container class="changelog">
            <span v-for="(change, index) in changes" :key="index" class="py-5">
                <p class="font-weight-bold">[{{ new Date(change.date)}}]</p>
                <span v-for="(statement, index) in change.statements" :key="index">
                    <p v-if="statement.html">
                        <span>{{change.modifier}} </span>
                        <span v-html="statement.description"></span>
                    </p>
                    <p v-else>{{change.modifier}} {{statement.description}}.<br></p>
                </span>
            </span>
        </v-container>
    </v-sheet>
</template>

<script>
export default {
    name: 'Changelog',
    props: {
        changes: Array
    },
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Source+Code+Pro:wght@500&display=swap');

.changelog {
    background-color: #e6dddd7e;
    font-family: 'Source Code Pro';
    font-size: 15px;
    overflow: auto;
}
</style>