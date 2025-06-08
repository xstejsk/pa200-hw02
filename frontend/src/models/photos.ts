export type NewPhoto = {
  file: File
}

export type PhotoFileDto = {
  createdAt: string;
  fileName: string;
  blobName: string;
  contentType: string;
}
