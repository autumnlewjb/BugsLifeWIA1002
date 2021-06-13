<template>
  <v-container>
    <UploadForm :dialog.sync="uploadDialog"
                :project-id="projectId"
                :issue-id="issueId"
                @update-attachment="fetchAttachments"/>
    <ConfirmDelete
        @toggleDeleteDialog="toggleDeleteDialog"
        :showDialog="confirmDeleteDialog"
    />
    <v-container v-if="this.projectId!==undefined">
      <v-row v-if="!editing" justify="end" class="mb-4">
        <v-btn class="ma-2 white--text text-right" elevation="3" rounded color="teal"
               @click="editing = !editing; selectedFileIndex=[];">
          <v-icon dark small left>mdi-pencil</v-icon>
          Edit
        </v-btn>
        <v-btn class="ma-2 white--text" elevation="3" rounded color="teal" @click="uploadDialog = true">
          + Add Attachment
        </v-btn>
      </v-row>
      <v-row v-else justify="end" class="mb-4">
        <v-btn class="ma-2 white--text text-right" elevation="3" rounded color="teal" @click="editing = !editing">
          <v-icon dark small left>mdi-window-close</v-icon>
          CANCEL
        </v-btn>
        <v-btn class="ma-2 white--text text-right" elevation="3" rounded color="red"
               @click="toggleDeleteDialog(false)">
          <v-icon dark small left>mdi-delete</v-icon>
          DELETE
        </v-btn>
      </v-row>
      <v-item-group v-model="selectedFileIndex" :multiple="editing">
        <v-row>
          <v-col v-for="(file, index) in attachments" :key="index" cols="3">
            <v-item v-slot="{ active, toggle }">
              <v-hover v-slot="{ hover }">
                <v-card max-width="220px" v-ripple="false"
                        :elevation="hover ? 12 : editing&&active ? 12 : 2"
                        :href="!editing ? file.path : 'javascript:void(0)'"
                        :target="!editing ? '_blank' : '_self'"
                        @click="toggle">
                  <v-img :src="file.path" :alt="file.fileName" class="fill-height align-end" height="150px">
                    <v-row style="height: 105px">
                      <v-col class="text-right">
                        <v-btn v-if="editing" icon dark>
                          <v-icon>
                            {{ active ? 'mdi-checkbox-marked' : 'mdi-checkbox-blank-outline' }}
                          </v-icon>
                        </v-btn>
                      </v-col>
                    </v-row>
                    <template v-if="loading" v-slot:placeholder>
                      <v-row class="fill-height ma-0 grey" align="center" justify="center">
                        <v-progress-circular indeterminate color="grey lighten-5"></v-progress-circular>
                      </v-row>
                    </template>
                    <template v-else-if="!loading" v-slot:placeholder>
                      <v-row class="fill-height ma-0 grey" align="center" justify="center">
                        <v-icon size="40" dark>{{ getIcon(file.fileName) }}</v-icon>
                      </v-row>
                    </template>
                    <div class="row">
                      <div class="col-10 text-truncate white--text ma-2"
                           :class="{'text--disabled': (!hover && !editing) }">
                        {{ file.fileName }}
                      </div>
                    </div>
                  </v-img>
                </v-card>
              </v-hover>
            </v-item>
          </v-col>
        </v-row>
      </v-item-group>
    </v-container>
    <v-container v-else-if="this.issueId!==undefined">
      <v-row class="mb-4 align-center">
        <v-btn text v-ripple="false" class="no-background-hover">
          <v-icon class="mr-1">mdi-attachment</v-icon>
          Attachment
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn v-if="!editing" fab dark x-small class="ma-2" elevation="3" color="teal"
               @click="editing = !editing; selectedFileIndex=[];">
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
        <v-btn v-if="!editing" fab dark x-small class="ma-2" elevation="3" color="teal" @click="uploadDialog = true">
          <v-icon>mdi-plus</v-icon>
        </v-btn>
        <v-btn v-if="!editing & !expand" fab dark x-small class="ma-2" elevation="3" color="teal"
               @click="expand = !expand">
          <v-icon>mdi-arrow-expand</v-icon>
        </v-btn>
        <v-btn v-if="editing" fab dark x-small class="ma-2" elevation="3" color="teal" @click="editing=false">
          <v-icon>mdi-window-close</v-icon>
        </v-btn>
        <v-btn v-if="editing" fab dark x-small class="ma-2" elevation="3" color="red"
               @click="toggleDeleteDialog(false)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
        <v-btn v-if="expand" fab dark x-small class="ma-2" elevation="3" color="teal" @click="expand=false">
          <v-icon>mdi-arrow-collapse</v-icon>
        </v-btn>
      </v-row>
      <v-slide-group v-if="!editing & !expand" show-arrows>
        <v-slide-item v-for="(file, index) in attachments" :key="index">
          <v-hover v-slot="{ hover }">
            <v-card :elevation="hover ? 4 : 0" class="ma-1" max-width="100" max-height="100">
              <a :href="file.path" target="_blank" style="text-decoration:none;">
                <v-img :src="file.path" :alt="file.fileName" class="fill-height align-end" min-height="100"
                       min-width="100">
                  <template v-if="loading" v-slot:placeholder>
                    <v-row class="fill-height ma-0 grey" align="center" justify="center">
                      <v-progress-circular indeterminate color="grey lighten-5"></v-progress-circular>
                    </v-row>
                  </template>
                  <template v-else-if="!loading" v-slot:placeholder class="fill-height">
                    <v-row class="fill-height ma-0 grey" align="center" justify="center">
                      <v-icon size="30" dark>{{ getIcon(file.fileName) }}</v-icon>
                    </v-row>
                  </template>
                  <div class="row">
                    <div class="col-10 text-truncate white--text ma-2">
                      {{ file.fileName }}
                    </div>
                  </div>
                </v-img>
              </a>
            </v-card>
          </v-hover>
        </v-slide-item>
      </v-slide-group>
      <v-item-group v-else-if="editing || expand" v-model="selectedFileIndex" multiple class="ma-0">
        <v-row>
          <v-col v-for="(file, index) in attachments" :key="index" cols="3">
            <v-item v-slot="{ active, toggle }">
              <v-card :elevation="active ? 4 : 0" max-width="120" max-height="120" @click="toggle"
                      :href="!editing ? file.path : 'javascript:void(0)'"
                      :target="!editing ? '_blank' : '_self'">
                <v-img :src="file.path" :alt="file.fileName" class="fill-height align-end justify-end"
                       min-height="100" min-width="100">
                  <v-row class="justify-end">
                    <v-btn v-if="editing" icon dark class="ma-4">
                      <v-icon>
                        {{ active ? 'mdi-checkbox-marked' : 'mdi-checkbox-blank-outline' }}
                      </v-icon>
                    </v-btn>
                  </v-row>
                  <template v-slot:placeholder>
                    <v-row class="fill-height ma-0 grey" align="center" justify="center">
                      <v-icon size="30" dark>{{ getIcon(file.fileName) }}</v-icon>
                    </v-row>
                  </template>
                  <div class="row">
                    <div class="col-10 text-truncate white--text ma-2">
                      {{ file.fileName }}
                    </div>
                  </div>
                </v-img>
              </v-card>
            </v-item>
          </v-col>
        </v-row>
      </v-item-group>
    </v-container>
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
      loading: true,
      uploadDialog: false,
      confirmDeleteDialog: false,
      editing: false,
      expand: false,
      url: undefined,
      attachments: [],
      selectedFileIndex: []
    }
  },
  created() {
    this.activateTimer()
    if (this.projectId != null) {
      this.url = `/api/project/${this.projectId}`
    } else {
      this.url = `/api/issue/${this.issueId}`
    }
    this.fetchAttachments();
  },
  methods: {
    activateTimer() {
      setTimeout(() => this.loading = false, 2000);
    },
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
    toggleDeleteDialog(userResponse) {
      this.confirmDeleteDialog = !this.confirmDeleteDialog;
      if (userResponse) {
        this.deleteFile();
      }
    },
    deleteFile() {
      let formData = new FormData();
      if (this.issueId != null) {
        formData.append("parent", "issue");
      } else {
        formData.append("parent", "project");
      }
      for (let i = 0; i < this.selectedFileIndex.length; i++) {
        formData.append("id", this.attachments[this.selectedFileIndex[i]].id);
      }
      fetch(`/api/file/del`, {
        method: 'DELETE',
        body: formData
      }).then((res) => {
        if (res.status === 200) {
          
          this.editing = false
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
.no-background-hover::before {
  background-color: transparent;
}
.v-btn:focus::before {
  opacity: 0 !important;
}
</style>