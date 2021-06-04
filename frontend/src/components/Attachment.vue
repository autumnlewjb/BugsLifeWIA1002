<template>
  <v-container>
    <v-row justify="end" class="mb-4">
      <v-btn class="ma-2 white--text text-right" elevation="3" rounded color="teal" @click="editing = !editing">
        <v-icon dark small left>mdi-pencil</v-icon>
        Edit
      </v-btn>
      <v-btn class="ma-2 white--text" elevation="3" rounded color="teal" @click="uploadDialog = true">
        + Add Attachment
      </v-btn>
    </v-row>
    <UploadForm :dialog.sync="uploadDialog"
                :project-id="projectId"
                :issue-id="issueId"
                @update-attachment="fetchAttachments"/>
    <v-container>
      <v-row>
        <v-col v-for="(file, index) in attachments" :key="index" cols="3">
          <v-hover v-slot="{ hover }">
            <v-card max-width="220px" :elevation="hover ? 12 : 2">
              <a :href="file.path" target="_blank" style="text-decoration:none;">
                <v-img :src="file.path" :alt="file.fileName" class="fill-height align-end" height="150px">
                  <template v-slot:placeholder>
                    <v-row class="fill-height ma-0 grey" align="center" justify="center">
                      <v-icon size="40" dark>{{ getIcon(file.fileName) }}</v-icon>
                    </v-row>
                  </template>
                  <div class="row">
                    <div class="col-10 text-truncate white--text ma-2"
                         :class="{'text--disabled': (!hover & !editing) }">
                      {{ file.fileName }}
                    </div>
                  </div>
                </v-img>
              </a>
              <v-card-actions v-if="editing" class="pa-0 ma-0">
                <v-spacer></v-spacer>
                <v-btn @click="toggleDeleteDialog(false,index)" right color="white" elevation="0">
                  DELETE
                  <v-icon color="red"> mdi-delete</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-hover>
        </v-col>
      </v-row>
    </v-container>
    <ConfirmDelete
        @toggleDeleteDialog="toggleDeleteDialog"
        :showDialog="confirmDeleteDialog"
    />
  </v-container>
</template>

<script>
import UploadForm from "./UploadForm";
import ConfirmDelete from "./ConfirmDelete";

export default {
  name: "Attachment",
  components: {
    UploadForm,
    ConfirmDelete
  },
  props: {
    projectId: String,
    issueId: Number
  },
  data() {
    return {
      uploadDialog: false,
      confirmDeleteDialog: false,
      indexToDelete: undefined,
      editing: false,
      url: undefined,
      attachments: []
    }
  },
  created() {
    if (this.projectId != null) {
      this.url = `/api/project/${this.projectId}`
    } else {
      this.url = `/api/issue/${this.issueId}`
    }
    this.fetchAttachments();
  },
  methods: {
    fetchAttachments() {
      fetch(this.url)
          .then((res) => {
            if (res.status === 200) {
              return res.json();
            } else {
              return null;
            }
          })
          .then((data) => {
            this.attachments = data.attachments;
          })
          .catch((e) => console.log(e));
    },
    toggleDeleteDialog(userResponse, index) {
      this.confirmDeleteDialog = !this.confirmDeleteDialog;
      if (index != null) {
        this.indexToDelete = index;
      }
      if (userResponse) {
        this.deleteFile(this.indexToDelete)
      }
    },
    deleteFile(index) {
      let formData = new FormData();
      if (this.issueId != null) {
        formData.append("parent", "issue");
      } else {
        formData.append("parent", "project");
      }
      formData.append("id", this.attachments[index].id);
      fetch(`/api/file/del`, {
        method: 'DELETE',
        body: formData
      }).then((res) => {
        if (res.status === 200) {
          console.log("Deleted the attachment")
          this.confirmDeleteDialog = false;
          this.fetchAttachments()
        } else {
          alert("Unable to delete the attachment")
        }
      }).catch((e) => console.log(e))
    },
    getIcon(filename) {
      if (/\.(jpe?g|png|gif)$/i.test(filename)) {
        return 'mdi-file-image'
      } else if (/\.txt$/i.test(filename)) {
        return 'mdi-file-document';
      } else {
        return 'mdi-file';
      }
    },
  },
}

</script>

<style scoped>
</style>