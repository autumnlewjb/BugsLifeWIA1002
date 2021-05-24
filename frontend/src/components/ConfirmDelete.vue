<template>
    <v-container>
        <v-dialog v-model="showDialog" persistent width="300">
            <v-card>
                <v-card-title>Deleting item...</v-card-title>
                <v-card-text>Do you wish to proceed? Your action cannot be undone after this.</v-card-text>
                <v-card-actions>
                    <v-btn plain color="red" @click="confirmDelete(true)">Delete</v-btn>
                    <v-btn plain @click="confirmDelete(false)">Abort</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
export default {
    props: {
        showDialog: Boolean
    },
    methods: {
        confirmDelete(userResponse) {
            this.$emit('toggleDeleteDialog', userResponse)
        }
    }

}
</script>