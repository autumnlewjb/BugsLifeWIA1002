<template>
    <v-container>
        <v-dialog v-model="dialog" persistent width="300">
            <v-card>
                <v-card-title>Don't be so mean....</v-card-title>
                <v-card-text>You should not be editing or deleting your collegues' work! </v-card-text>
                <v-card-actions>
                    <v-btn plain @click="onClick">
                        I'm sorry...
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
export default {
    props: {
        dialog: Boolean
    },
    methods: {
        onClick() {
            this.$emit('closeDialog');
        }
    }
}
</script>