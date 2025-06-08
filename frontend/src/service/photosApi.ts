import axiosInstance from "./base";
import {PhotoFileDto} from "../models/photos.ts";

const create = async (photoFile: FormData): Promise<PhotoFileDto> => {
  const response = await axiosInstance.post("/api/photos/upload", photoFile, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return response.data;
};

const getAll = async (): Promise<PhotoFileDto[]> => {
  const response = await axiosInstance.get("/api/photos");
  return response.data;
};

const getPhotoSasUrl = async (blobName: string): Promise<string> => {
  const response = await axiosInstance.get(`/api/photos/${blobName}/sas-url`);
  return response.data;
}

export default {
  create,
  getAll,
  getPhotoSasUrl
};
