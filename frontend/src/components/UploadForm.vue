<template>
  <v-dialog @click:outside="closeDialog" :value="dialog" max-width="1000px">
    <v-card @drop.prevent="onDrop($event)"
            @dragover.prevent="dragover = true"
            @dragenter.prevent="dragover = true"
            @dragleave.prevent="dragover = false"
            :class="{ 'blue lighten-4': dragover }"
            class="pa-16">
      <v-card-text class="pt-16" v-if="files.length === 0">
        <v-row justify="center">
          <v-icon size="60">
            mdi-cloud-upload
          </v-icon>
        </v-row>
        <v-row justify="center">
          Drag and drop your attachment(s) here.
        </v-row>
      </v-card-text>
      <v-list class="pa-6" v-else-if="files.length > 0" flat>
        <v-list-group v-for="(file, index) in files" :key="index">
          <template v-slot:activator>
            <v-list-item-action>
              <v-btn @click="removeFile(index)" icon>
                <v-icon> mdi-close-circle</v-icon>
              </v-btn>
            </v-list-item-action>
            <v-list-item-content>
              <v-row>
                <v-col>
                  {{ file.name }}
                </v-col>
                <v-col cols="3" class="text-center text--secondary">
                  {{ file.size }} bytes
                </v-col>
              </v-row>
              <v-divider></v-divider>
            </v-list-item-content>
          </template>
          <v-img :src="src[index]" :alt="file.name" contain class="ma-4">
          </v-img>
        </v-list-group>
      </v-list>
      <v-card-text class="justify-end">
        <v-row class="pa-6" align="center">
          <v-col>
            <v-file-input
                v-model="files"
                multiple
                show-size
                label="Select Files"
                @change="selectFiles"
            ></v-file-input>
          </v-col>
          <v-btn :loading="loading"
                 :disabled="loading"
                 rounded
                 color="primary"
                 @click="upload">
            Upload
            <v-icon right dark>mdi-cloud-upload</v-icon>
          </v-btn>
        </v-row>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "UploadForm",
  data() {
    return {
      dragover: false,
      files: [],
      src: [],
      loading: false,
    };
  },
  props: {
    dialog: {
      type: Boolean,
      required: true
    },
    projectId: String,
    issueId: Number
  },
  methods: {
    closeDialog() {
      this.files = [];
      this.$emit("update:dialog", false);
    },
    selectFiles(files) {
      this.files = files;
    },
    onDrop(e) {
      this.dragover = false;
      for (let i = 0; i < e.dataTransfer.files.length; i++) {
        this.files.push(e.dataTransfer.files[i])
      }
      this.getImagePreviews()
    },
    upload() {
      if (this.files.length !== 0) {
        this.loading = true;
        let formData = new FormData();
        if (this.issueId != null) {
          formData.append("parent", "issue");
          formData.append("id", this.issueId.toString());
        } else {
          formData.append("parent", "project");
          formData.append("id", this.projectId);
        }
        for (let i = 0; i < this.files.length; i++) {
          formData.append("files", this.files[i]);
        }
        return fetch(`/api/upload`, {
          method: 'POST',
          body: formData
        }).then((res) => {
          if (res.status === 200) {
            console.log("Uploaded the attachment")
            this.$emit("update-attachment")
            this.closeDialog()
          } else {
            alert("Cannot upload the attachment")
          }
        }).catch((e) => console.log(e))
            .finally(() => this.loading = false)
      }
    },
    removeFile(key) {
      this.files.splice(key, 1);
      this.getImagePreviews()
    },
    getImagePreviews() {
      this.src = [];
      for (let i = 0; i < this.files.length; i++) {
        if (/\.(jpe?g|png|gif)$/i.test(this.files[i].name)) {
          let reader = new FileReader();
          reader.addEventListener("load", function () {
            this.src.push(reader.result);
          }.bind(this), false);
          reader.readAsDataURL(this.files[i]);
        } else {
          this.$nextTick(function () {
            this.$refs['preview' + parseInt(i)][0].src = '/images/attachment.png';
          });
        }
      }
    },
  },
};
</script>

<style scoped>

</style>